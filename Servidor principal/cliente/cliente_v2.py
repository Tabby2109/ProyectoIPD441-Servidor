import requests
import os
import base64
from PIL import Image
import io

# Ruta de la imagen que deseas enviar
ruta_imagen =  "../data_clip/portadas_data_clip/Cinder - Marissa Meyer.jpg"

# Modo: "clip" o "yolo+clip"
modo = "clip"  # o "yolo+clip"

# URL del servidor
url = "http://127.0.0.1:5000/predict"

# Enviar imagen al servidor
with open(ruta_imagen, "rb") as f:
    files = {"image": f}
    data = {"modo": modo}
    response = requests.post(url, files=files, data=data)

# Procesar respuesta
if response.status_code == 200:
    resultado = response.json()

    print("✅ Resultado recibido:")
    print(f"📘 Texto más parecido : {resultado['texto_mas_parecido']}")
    print(f"📗 Título              : {resultado['titulo']}")
    print(f"🧑‍🏫 Autor/es           : {resultado['autor']}")
    print(f"🏷️ Género/s            : {resultado['genero']}")
    print(f"📝 Descripción         : {resultado['descripcion'][:120]}...")
    print(f"📊 Similitud           : {resultado['similitud']}")
    print(f"🧠 Modo utilizado      : {resultado['modo']}")

    # Imagen con bounding box si corresponde
    if "imagen_con_bbox" in resultado:
        print("🖼️ Imagen con detección recibida.")
        img_data = base64.b64decode(resultado["imagen_con_bbox"])
        img = Image.open(io.BytesIO(img_data))
        img.save("resultado_bbox.jpg")
        img.show()

    # Mostrar links de compra
    if "links_compra" in resultado and resultado["links_compra"]:
        print("\n🛒 Links de compra recomendados:")
        for i, link in enumerate(resultado["links_compra"], 1):
            origen = link.get("origen", "Desconocido")
            precio = link.get("precio", "N/A")
            similitud = link.get("similitud", "N/A")
            url_compra = link.get("url", "N/A")
            print(f"{i}. [{origen}] ${precio} - Similitud: {similitud} → {url_compra}")
    else:
        print("❌ No se encontraron links de compra.")
else:
    print("❌ Error:")
    print(response.text)
