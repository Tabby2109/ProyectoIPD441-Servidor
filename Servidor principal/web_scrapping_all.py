import os
import re
import csv
import json
import time
import random
import requests
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait

# ========== CONFIGURACIÓN ========== #
CARPETA_PORTADAS = "data_clip/portadas_data_clip"
CSV_FILE = "libros.csv"
CHECKPOINT_FILE = "checkpoint.txt"

if not os.path.exists(CARPETA_PORTADAS):
    os.makedirs(CARPETA_PORTADAS)

# ========== FUNCIONES ========== #

def iniciar_driver():
    opciones = Options()
    opciones.add_argument("--window-size=1920,1080")
    opciones.add_argument("--user-data-dir=C:\\Temp\\selenium")
    opciones.add_argument("--disable-extensions")
    opciones.add_argument("--incognito")
    opciones.add_experimental_option('excludeSwitches', ['enable-logging'])
    return webdriver.Chrome(options=opciones)

def guardar_portada(url_imagen, titulo, autor_principal):
    nombre_archivo = f"{titulo} - {autor_principal}"
    nombre_archivo = re.sub(r'[\\/*?:"<>|]', "", nombre_archivo) + ".jpg"
    ruta = os.path.join(CARPETA_PORTADAS, nombre_archivo)
    try:
        respuesta = requests.get(url_imagen, timeout=15)
        if respuesta.status_code == 200:
            with open(ruta, "wb") as f:
                f.write(respuesta.content)
            return ruta
    except Exception as e:
        print(f"Error al descargar imagen: {e}")
    return ""

def guardar_en_csv(fila):
    archivo_existe = os.path.exists(CSV_FILE)
    with open(CSV_FILE, "a", encoding="utf-8-sig", newline="") as f:
        writer = csv.writer(f)
        if not archivo_existe:
            encabezados = ["Título", "Autor/es", "Género/s", "Descripción", "Ruta portada"]
            writer.writerow(encabezados)
        writer.writerow(fila)

def procesar_libro(driver, url_libro):
    try:
        driver.get(url_libro)
        time.sleep(2)
        soup = BeautifulSoup(driver.page_source, "html.parser")

        titulo_tag = soup.select_one("div#title h1")
        titulo = titulo_tag.text.strip() if titulo_tag else "Desconocido"

        autores = []
        bloque_autores = soup.find("div", id="autor")
        if bloque_autores:
            autores = [a.text.strip() for a in bloque_autores.find_all("a")]
        autor_principal = autores[0] if autores else "Desconocido"

        generos = []
        bloque_generos = soup.find("div", id="genero")
        if bloque_generos:
            generos = [a.text.strip() for a in bloque_generos.find_all("a")]

        descripcion = ""
        bloque_desc = soup.find("div", id="sinopsis")
        if bloque_desc:
            # Intenta primero con div.ali_justi
            texto = bloque_desc.find("div", class_="ali_justi")
            if texto:
                descripcion = texto.get_text(separator=" ").strip()
            else:
                # Si no existe ese div, intenta tomar el texto de todo el bloque
                descripcion = bloque_desc.get_text(separator=" ").strip()


        ruta_imagen = ""
        img_tag = soup.select_one("div#cover img")
        if img_tag and img_tag.get("src"):
            url_imagen = img_tag["src"]
            ruta_imagen = guardar_portada(url_imagen, titulo, autor_principal)

        fila = [titulo, ", ".join(autores), ", ".join(generos), descripcion, ruta_imagen]
        guardar_en_csv(fila)
        print(f"Guardado: {titulo}")

    except Exception as e:
        print(f"Error procesando libro {url_libro}: {e}")

def cargar_checkpoint():
    if not os.path.exists(CHECKPOINT_FILE):
        return None, None, None
    with open(CHECKPOINT_FILE, "r", encoding="utf-8") as f:
        linea = f.read().strip()
        if not linea:
            return None, None, None
        return linea.split("|")

def guardar_checkpoint(letra, autor, libro_index):
    with open(CHECKPOINT_FILE, "w", encoding="utf-8") as f:
        f.write(f"{letra}|{autor}|{libro_index}")

# ========== MAIN ========== #

checkpoint_letra, checkpoint_autor, checkpoint_libro = cargar_checkpoint()
if checkpoint_libro is not None:
    checkpoint_libro = int(checkpoint_libro)

driver = iniciar_driver()
driver.get("https://ww3.lectulandia.com/?loggedout=true")
time.sleep(3)

WebDriverWait(driver, 10).until(lambda d: d.execute_script("return typeof jQuery !== 'undefined';"))
WebDriverWait(driver, 10).until(lambda d: d.find_element("class name", "abc"))

soup = BeautifulSoup(driver.page_source, "html.parser")
div_abc = soup.find("div", class_="abc")
letras = [span.text.strip() for span in div_abc.find_all("span", class_="taxLetra")]

saltando_letras = checkpoint_letra is not None
for letra in letras:
    if saltando_letras:
        if letra != checkpoint_letra:
            print(f"⏭️ Saltando letra {letra}")
            continue
        else:
            saltando_letras = False

    print(f"\n🔤 Procesando letra: {letra}")

    driver.execute_script(f"""
        window.lectuAutores = null;
        jQuery.post('https://ww3.lectulandia.com/wp-admin/admin-ajax.php', {{
            action: 'PDOTaxonomies',
            letra: '{letra}',
            term: 'autor'
        }}, function(data) {{
            window.lectuAutores = data;
        }});
    """)

    try:
        WebDriverWait(driver, 10).until(lambda d: d.execute_script("return window.lectuAutores !== null"))
        autores_raw = driver.execute_script("return window.lectuAutores;")
        if isinstance(autores_raw, str):
            autores_dict = json.loads(autores_raw)
        else:
            autores_dict = autores_raw

        lista_autores = list(autores_dict.values())

    except Exception as e:
        print(f"⚠️ No se pudieron obtener autores para letra {letra}: {e}")
        continue

    saltando_autores = checkpoint_autor is not None
    for nombre_autor in lista_autores:
        if saltando_autores:
            if nombre_autor != checkpoint_autor:
                print(f"⏭️ Saltando autor {nombre_autor}")
                continue
            else:
                saltando_autores = False

        url_autor = f"https://ww3.lectulandia.com/autor/{nombre_autor}"
        print(f"✍️ Autor: {nombre_autor}")
        try:
            driver.get(url_autor)
            #time.sleep(2)
            soup_autor = BeautifulSoup(driver.page_source, "html.parser")
            libros = soup_autor.find_all("article", class_="card")

            saltando_libros = checkpoint_libro is not None
            for i, libro in enumerate(libros):
                if saltando_libros:
                    if i < checkpoint_libro:
                        print(f"⏭️ Saltando libro #{i}")
                        continue
                    else:
                        saltando_libros = False

                titulo = libro.find("img", class_="cover")["alt"].strip()
                enlace = libro.find("a", class_="title")["href"].strip()
                url_libro = f"https://ww3.lectulandia.com{enlace}"
                print(f"📚 Libro: {titulo}")

                procesar_libro(driver, url_libro)
                guardar_checkpoint(letra, nombre_autor, i + 1)
                #time.sleep(random.uniform(1.5, 3.0))

            checkpoint_libro = None  # fin de autor

        except Exception as e:
            print(f"⚠️ Error con autor {nombre_autor}: {e}")

    checkpoint_autor = None  # fin de letra

driver.quit()
print("\n🎉 Scraping finalizado.")
