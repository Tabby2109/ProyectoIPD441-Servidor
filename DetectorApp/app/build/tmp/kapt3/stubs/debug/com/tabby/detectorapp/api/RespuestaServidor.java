package com.tabby.detectorapp.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\tH\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0003Jk\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0001J\u0013\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010)\u001a\u00020*H\u00d6\u0001J\t\u0010+\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011\u00a8\u0006,"}, d2 = {"Lcom/tabby/detectorapp/api/RespuestaServidor;", "", "texto_mas_parecido", "", "titulo", "autor", "genero", "descripcion", "similitud", "", "modo", "imagen_procesada", "linksCompra", "", "Lcom/tabby/detectorapp/api/LinkCompra;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getAutor", "()Ljava/lang/String;", "getDescripcion", "getGenero", "getImagen_procesada", "getLinksCompra", "()Ljava/util/List;", "getModo", "getSimilitud", "()D", "getTexto_mas_parecido", "getTitulo", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class RespuestaServidor {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String texto_mas_parecido = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String titulo = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String autor = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String genero = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String descripcion = null;
    private final double similitud = 0.0;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String modo = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String imagen_procesada = null;
    @com.google.gson.annotations.SerializedName(value = "links_compra")
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.tabby.detectorapp.api.LinkCompra> linksCompra = null;
    
    public RespuestaServidor(@org.jetbrains.annotations.NotNull
    java.lang.String texto_mas_parecido, @org.jetbrains.annotations.NotNull
    java.lang.String titulo, @org.jetbrains.annotations.NotNull
    java.lang.String autor, @org.jetbrains.annotations.NotNull
    java.lang.String genero, @org.jetbrains.annotations.NotNull
    java.lang.String descripcion, double similitud, @org.jetbrains.annotations.NotNull
    java.lang.String modo, @org.jetbrains.annotations.Nullable
    java.lang.String imagen_procesada, @org.jetbrains.annotations.NotNull
    java.util.List<com.tabby.detectorapp.api.LinkCompra> linksCompra) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getTexto_mas_parecido() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getTitulo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAutor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getGenero() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDescripcion() {
        return null;
    }
    
    public final double getSimilitud() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getModo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getImagen_procesada() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.tabby.detectorapp.api.LinkCompra> getLinksCompra() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.tabby.detectorapp.api.LinkCompra> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.tabby.detectorapp.api.RespuestaServidor copy(@org.jetbrains.annotations.NotNull
    java.lang.String texto_mas_parecido, @org.jetbrains.annotations.NotNull
    java.lang.String titulo, @org.jetbrains.annotations.NotNull
    java.lang.String autor, @org.jetbrains.annotations.NotNull
    java.lang.String genero, @org.jetbrains.annotations.NotNull
    java.lang.String descripcion, double similitud, @org.jetbrains.annotations.NotNull
    java.lang.String modo, @org.jetbrains.annotations.Nullable
    java.lang.String imagen_procesada, @org.jetbrains.annotations.NotNull
    java.util.List<com.tabby.detectorapp.api.LinkCompra> linksCompra) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}