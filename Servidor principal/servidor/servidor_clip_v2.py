from flask import Flask, request, jsonify
import torch
import pickle
import open_clip
import numpy as np
from PIL import Image, ImageOps, ImageFilter, ImageDraw
import io
from ultralytics import YOLO
import os
import pandas as pd
import base64
from links_compra import obtener_links_ordenados  # <- Asegúrate que esté en el mismo directorio o en tu PYTHONPATH

app = Flask(__name__)

# -------------------- Cargar embeddings y modelo CLIP --------------------
ruta_actual = os.path.dirname(__file__)
ruta_pkl = os.path.abspath(os.path.join(ruta_actual, "../pkl/embeddings_clip_full (1).pkl"))
ruta_csv = os.path.abspath(os.path.join(ruta_actual, "../data_clip/data_clip_limpia.csv"))

print(f"Cargando embeddings desde: {ruta_pkl}")
with open(ruta_pkl, "rb") as f:
    datos = pickle.load(f)

image_features_dataset = datos["image_features"]
text_features_dataset = datos["text_features"]
textos = datos["textos"]
rutas = datos["rutas_imagenes"]

print("Cargando base de datos CSV...")
df = pd.read_csv(ruta_csv)
df["titulo_autor"] = df["Título"].astype(str).str.strip() + " - " + df["Autor/es"].astype(str).str.strip()

model, preprocess, tokenizer = open_clip.create_model_and_transforms(
    model_name="ViT-B-32",
    pretrained="laion2b_s34b_b79k"
)
device = "cuda" if torch.cuda.is_available() else "cpu"
model = model.to(device).eval()

# -------------------- Cargar modelo YOLO --------------------
ruta_yolo = os.path.abspath(os.path.join(ruta_actual, "../data_yolo/runs/detect/data_yolo11/weights/best.pt"))
modelo_yolo = YOLO(ruta_yolo)

# -------------------- Detección y recorte + dibujo de bounding box --------------------
def detectar_portada_y_mejorar(image_pil):
    img_np = np.array(image_pil)
    results = modelo_yolo.predict(img_np, conf=0.4)
    boxes = results[0].boxes

    if len(boxes) == 0:
        return None, None

    best_idx = boxes.conf.argmax()
    x1, y1, x2, y2 = boxes.xyxy[best_idx].tolist()
    x1, y1, x2, y2 = map(int, [x1, y1, x2, y2])

    imagen_con_bbox = image_pil.copy()
    draw = ImageDraw.Draw(imagen_con_bbox)
    draw.rectangle([x1, y1, x2, y2], outline="red", width=4)

    portada_crop = image_pil.crop((x1, y1, x2, y2))
    portada_crop = ImageOps.fit(portada_crop, (256, 256), Image.LANCZOS)
    portada_crop = portada_crop.filter(ImageFilter.SMOOTH_MORE)

    return portada_crop, imagen_con_bbox

# -------------------- Embedding + comparación CLIP --------------------
def obtener_resultado_clip(img_pil):
    img_tensor = preprocess(img_pil).unsqueeze(0).to(device)
    with torch.no_grad():
        img_feat = model.encode_image(img_tensor)
        img_feat /= img_feat.norm(dim=-1, keepdim=True)
    cos_sim = torch.nn.functional.cosine_similarity(img_feat.cpu(), text_features_dataset)
    idx = torch.argmax(cos_sim).item()
    return textos[idx], round(cos_sim[idx].item(), 4)

# -------------------- Buscar información desde texto --------------------
def buscar_info(texto_predicho):
    coincidencias = df[df["titulo_autor"].str.lower().str.strip() == texto_predicho.lower().strip()]
    if not coincidencias.empty:
        fila = coincidencias.iloc[0]
        return {
            "titulo": fila["Título"],
            "autor": fila["Autor/es"],
            "genero": fila["Género/s"],
            "descripcion": fila["Descripción"]
        }
    else:
        return {
            "titulo": "No encontrado",
            "autor": "No encontrado",
            "genero": "No encontrado",
            "descripcion": "No encontrado"
        }

# -------------------- Codificar imagen como base64 --------------------
def imagen_a_base64(imagen):
    buffer = io.BytesIO()
    imagen.save(buffer, format="JPEG")
    return base64.b64encode(buffer.getvalue()).decode("utf-8")

# -------------------- Endpoint --------------------
@app.route("/predict", methods=["POST"])
def predict():
    if 'image' not in request.files:
        return jsonify({"error": "No se recibió ninguna imagen."}), 400

    modo = request.form.get("modo", "clip").lower()
    file = request.files["image"]

    try:
        img_pil = Image.open(file.stream).convert("RGB")

        imagen_base64 = None
        if modo == "yolo+clip":
            portada, imagen_bbox = detectar_portada_y_mejorar(img_pil)
            if portada is None:
                return jsonify({"error": "No se detectó ninguna portada con YOLO."}), 404
            img_a_usar = portada
            imagen_base64 = imagen_a_base64(imagen_bbox)
        else:
            img_a_usar = img_pil

        texto, similitud = obtener_resultado_clip(img_a_usar)
        info = buscar_info(texto)

        respuesta = {
            "texto_mas_parecido": texto,
            "similitud": similitud,
            "titulo": info["titulo"],
            "autor": info["autor"],
            "genero": info["genero"],
            "descripcion": info["descripcion"],
            "modo": modo
        }

        if imagen_base64:
            respuesta["imagen_con_bbox"] = imagen_base64

        # -------------------- Agregar links de compra --------------------
        if info["titulo"] != "No encontrado" and info["autor"] != "No encontrado":
            df_links = obtener_links_ordenados(img_a_usar, info["titulo"], info["autor"])
            top_links = df_links.to_dict(orient="records")[:5]
            respuesta["links_compra"] = top_links
        else:
            respuesta["links_compra"] = []

        return jsonify(respuesta)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# -------------------- Lanzar servidor --------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
