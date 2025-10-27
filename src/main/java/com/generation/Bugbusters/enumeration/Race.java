package com.generation.Bugbusters.enumeration;

/*
 * corrisponde all'enum sql race
 * meglio usare le enum per le scelte fisse della scheda personaggio
 * questo è molto più pulito e sicuro (sempre per una questione di type-safe) che usare stringhe
 * in futuro, se servono altre razze, basta aggiungerle qui
 */
public enum Race {
    HUMAN,
    ELF,
    DWARF,
    HALFLING,
    ORC,
    GNOME,
    TIEFLING,
    DRAGONBORN,
    HALF_ELF,
    HALF_ORC
}