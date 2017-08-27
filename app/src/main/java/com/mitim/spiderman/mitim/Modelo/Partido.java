package com.mitim.spiderman.mitim.Modelo;

/**
 * Created by Spiderman on 8/11/2017.
 */

public class Partido
{
    private String titulo,lugar,hora,jugadores,creador;

    public Partido() {
    }

    public Partido(String titulo, String lugar, String hora) {
        this.titulo = titulo;
        this.lugar = lugar;
        this.hora = hora;
    }

    public String getJugadores() {
        return jugadores;
    }

    public void setJugadores(String jugadores) {
        this.jugadores = jugadores;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
