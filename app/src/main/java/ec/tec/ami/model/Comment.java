package ec.tec.ami.model;

import java.util.Date;
public class Comment {
    private User usuario;
    private Date fecha;
    private String comentario;

    public Comentario(User usuario, Date fecha, String comentario) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.comentario = comentario;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
