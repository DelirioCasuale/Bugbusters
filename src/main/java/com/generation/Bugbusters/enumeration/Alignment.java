package com.generation.Bugbusters.enumeration;

/*
 * corrisponde all'enum sql alignment
 * meglio usare le enum per le scelte fisse della scheda personaggio
 * questo è molto più pulito e sicuro (sempre per una questione di type-safe) che usare stringhe
 * in futuro, se servono altre allineamenti, basta aggiungerli qui
 */
public enum Alignment {
    LAWFUL_GOOD,
    NEUTRAL_GOOD,
    CHAOTIC_GOOD,
    LAWFUL_NEUTRAL,
    TRUE_NEUTRAL,
    CHAOTIC_NEUTRAL,
    LAWFUL_EVIL,
    NEUTRAL_EVIL,
    CHAOTIC_EVIL
}