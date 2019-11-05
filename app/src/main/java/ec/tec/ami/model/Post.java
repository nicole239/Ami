package ec.tec.ami.model;


import java.util.Date;
import java.util.List;

public class Post {

    private String usuario;
    private String descripcion;
    private String multimtdia;
    private Date fecha;
    private List<String> likes;
    private List<String> dislikes;
    private List<Comentario> comentarios;

    public Post(String usuario, String descripcion, String multimtdia, Date fecha, List<String> likes, List<String> dislikes, List<Comentario> comentarios) {
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.multimtdia = multimtdia;
        this.fecha = fecha;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comentarios = comentarios;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMultimtdia() {
        return multimtdia;
    }

    public void setMultimtdia(String multimtdia) {
        this.multimtdia = multimtdia;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public int getTotalLikes(){
        return likes.size();
    }

    public int getTotalDislikes(){
        return dislikes.size();
    }
}
