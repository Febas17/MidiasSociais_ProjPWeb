package br.edu.faculdade.academico.service;

public final class GeradorMatricula {
    private GeradorMatricula() {}

    public static String gerar(long numero) {
        if (numero < 0 || numero > 999999) {
            throw new IllegalArgumentException("Número-base fora do intervalo 000000..999999");
        }
        String base = String.format(java.util.Locale.ROOT, "%06d", numero);
        int[] pesos = {7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 6; i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int digito = 11 - (soma % 11);
        if (digito >= 10) digito = 0;
        return base + "-" + digito;
    }
}
