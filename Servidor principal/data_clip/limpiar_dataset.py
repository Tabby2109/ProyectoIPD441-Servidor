import pandas as pd

# Ruta al archivo original
archivo_entrada = "data_clip/data_clip.xlsx"
archivo_salida = "data_clip/data_clip_limpia.csv"

# Cargar el archivo Excel
df = pd.read_excel(archivo_entrada)

# 1. Eliminar duplicados (toda la fila debe ser duplicada para considerarse)
df = df.drop_duplicates()

# 2. Eliminar filas con columna B vacía (columna en índice 1 si no tiene nombre)
# Si tus columnas tienen nombres, puedes usar df['NombreColumna']
df = df[df.iloc[:, 1].notna()]

# 3. Limpiar saltos de línea en la columna D (índice 3 o nombre de columna si tiene)
df.iloc[:, 3] = df.iloc[:, 3].astype(str).str.replace(r'[\n\r]+', ' ', regex=True)

# 4. Eliminar el texto específico después de la descripción
texto_a_eliminar = "IMPORTANTE:  Recomendamos descargar los libros de poesía en formato EPUB dado que en PDF pueden tener problemas de visualización."
df.iloc[:, 3] = df.iloc[:, 3].str.replace(texto_a_eliminar, '', regex=False)

# Guardar el resultado en un nuevo archivo
df.to_csv(archivo_salida, index=False, encoding="utf-8-sig")

print("Archivo limpiado y guardado como:", archivo_salida)
