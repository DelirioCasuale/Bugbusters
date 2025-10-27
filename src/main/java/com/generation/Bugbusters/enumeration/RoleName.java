package com.generation.Bugbusters.enumeration;

/*
 * definisce i ruoli statici presenti nel sistema, corrisponde ai valori inseriti nello script SQL nella tabella 'roles'
 * ROLE_ADMIN: ruolo per gli amministratori del sistema
 * ROLE_USER: ruolo per gli utenti base, che include sia Player che Master
 * 
 * l'enumerazione serve a garantire che i ruoli siano gestiti in modo consistente in tutto il codice
 * questo aiuta a prevenire errori di battitura e facilita la manutenzione del codice
 */
public enum RoleName {
    ROLE_ADMIN,
    ROLE_USER
}