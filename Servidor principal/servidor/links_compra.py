import requests
from bs4 import BeautifulSoup
from PIL import Image
from io import BytesIO
import pandas as pd
import numpy as np
import torch
import open_clip
import time
import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# -------------------- CLIP Config --------------------
model, preprocess, _ = open_clip.create_model_and_transforms("ViT-B-32", pretrained="laion2b_s34b_b79k")
device = "cuda" if torch.cuda.is_available() else "cpu"
model = model.to(device).eval()

def embed_image(pil_image):
    image_tensor = preprocess(pil_image).unsqueeze(0).to(device)
    with torch.no_grad():
        image_features = model.encode_image(image_tensor)
        image_features /= image_features.norm(dim=-1, keepdim=True)
    return image_features.cpu().numpy()[0]

# -------------------- Google Books --------------------
def buscar_google_books(titulo, autor):
    query = f"{titulo} {autor}"
    url = f"https://www.googleapis.com/books/v1/volumes?q={requests.utils.quote(query)}"
    response = requests.get(url)
    items = response.json().get("items", [])[:5]
    libros = []

    for item in items:
        info = item.get("volumeInfo", {})
        sale = item.get("saleInfo", {})
        enlace = sale.get("buyLink", info.get("infoLink", ""))
        img_url = info.get("imageLinks", {}).get("thumbnail", "")
        precio = sale.get("retailPrice", {}).get("amount", None)

        libros.append({
            "origen": "Google Books",
            "img_url": img_url,
            "precio": precio,
            "url": enlace
        })

    return libros

# -------------------- BuscaLibre --------------------
def iniciar_driver():
    opciones = uc.ChromeOptions()
    opciones.add_argument("--window-size=1920,1080")
    opciones.add_argument("--disable-extensions")
    opciones.add_argument("--incognito")
    opciones.add_argument("--no-sandbox")
    opciones.add_argument("--disable-blink-features=AutomationControlled")
    return uc.Chrome(options=opciones, headless=False)

def obtener_links_buscalibre(titulo, autor):
    query = f"{titulo} {autor}".replace(" ", "+").lower()
    url = f"https://www.buscalibre.cl/libros/search/?q={query}"
    print(f"🔍 Buscando: {url}")

    driver = iniciar_driver()
    links = []

    try:
        driver.get(url)

        # Esperar a que cargue al menos un resultado
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "producto"))
        )

        # Parsear con BeautifulSoup
        soup = BeautifulSoup(driver.page_source, "html.parser")
        resultados = soup.find_all("div", class_="producto")

        for producto in resultados:
            a_tag = producto.find("a", href=True)
            if a_tag:
                link = a_tag['href']
                if not link.startswith("http"):
                    link = "https://www.buscalibre.cl" + link  # completar enlace
                links.append(link)

        print(f"✅ Se encontraron {len(links)} resultados.")
        return links

    except Exception as e:
        print(f"❌ Error al obtener links: {e}")
        return []

    finally:
        driver.quit()


def extraer_info_buscalibre(driver, url):
    try:
        driver.get(url)
        time.sleep(2)
        soup = BeautifulSoup(driver.page_source, "html.parser")

        img_tag = soup.find("meta", property="og:image")
        img_url = img_tag["content"] if img_tag else None

        script = soup.find("script", string=lambda t: t and "precio_producto" in t)
        precio = None
        if script:
            try:
                texto = script.string
                precio_str = texto.split("precio_producto': '")[1].split("'")[0]
                precio = int(precio_str)
            except:
                pass

        return {"origen": "BuscaLibre", "img_url": img_url, "precio": precio, "url": url}
    except:
        return {"origen": "BuscaLibre", "img_url": None, "precio": None, "url": url}

# -------------------- Comparación por similitud --------------------
def comparar_libros(img_input_pil, libros):
    emb_input = embed_image(img_input_pil)
    resultados = []

    for libro in libros:
        if not libro["img_url"]:
            continue
        try:
            resp = requests.get(libro["img_url"], timeout=10)
            if resp.status_code != 200:
                continue
            img = Image.open(BytesIO(resp.content)).convert("RGB")
            emb_libro = embed_image(img)
            similitud = np.dot(emb_input, emb_libro)

            resultados.append({
                "origen": libro["origen"],
                "precio": libro["precio"] if libro["precio"] is not None else 999999,
                "similitud": round(similitud, 4),
                "url": libro["url"]
            })
        except:
            continue

    return pd.DataFrame(resultados).sort_values(by=["similitud", "precio"], ascending=[False, True])

# -------------------- MAIN --------------------
def obtener_links_ordenados(imagen_pil, titulo, autor):
    """
    Devuelve un DataFrame ordenado por similitud y precio (de menor a mayor),
    combinando resultados de Google Books y BuscaLibre.
    """
    libros_google = buscar_google_books(titulo, autor)

    driver = iniciar_driver()
    links = obtener_links_buscalibre(titulo, autor)[:5]
    libros_bl = [extraer_info_buscalibre(driver, link) for link in links]
    driver.quit()

    libros_totales = libros_google + libros_bl
    df = comparar_libros(imagen_pil, libros_totales)
    return df
