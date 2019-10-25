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
}
