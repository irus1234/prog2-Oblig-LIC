package uy.edu.um.doors;

public class Usuario {

    private final int uid;
    private final String alias;
    private final TipoUsuario tipo;

    public Usuario(int uid, String alias, TipoUsuario tipo) {
        if (uid < 0) {
            throw new IllegalArgumentException("UID inválido");
        }

        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias inválido");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de usuario inválido");
        }

        this.uid = uid;
        this.alias = alias.trim();
        this.tipo = tipo;
    }

    public int getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public int getPeso() {
        if (tipo == TipoUsuario.ADMIN) {
            return 32;
        }

        return 16;
    }

    @Override
    public String toString() {
        return "USER:" + alias + " UID:" + uid + " TYPE:" + tipo;
    }
}