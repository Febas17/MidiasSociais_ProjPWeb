package br.edu.faculdade.bd.jpa;


import org.h2.tools.Server;

public class H2Console {

    public static void main(String[] args) throws Exception {
        // Inicia o console web (localhost:8082)
        Server.createWebServer("-web", "-webAllowOthers").start();

        // (Opcional) também abre servidor TCP, útil p/ conectar de fora
        // Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        System.out.println("H2 Console em http://localhost:8082");
        System.out.println("Use sua URL JDBC, ex.: jdbc:h2:file:./database/escola;AUTO_SERVER=TRUE");
    }
}
