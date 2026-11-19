/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package br.edu.faculdade.bd.jpa;

import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;



public class BDJPA {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BDJPA");
        emf.close();
        System.out.println("JPA inicializado. Se o persistence.xml está correto, o schema/tabelas foram criados.");
    }
}
