# Canvas rasterization app

Projekt do PGRF1 - rasterizace úsečky a polygonu, vyplňování (seed fill, scan line, i vzorem) a ořezávání (Cohen-Sutherland, Sutherland-Hodgman).

## Co je potřeba

- **JDK 23** (minimálně 22, v kódu se používá `_` pro nepoužité parametry lambd)
- knihovny jsou přímo v projektu ve složce `lib/`, takže není potřeba Maven ani internet:
  - `gson-2.12.1.jar` - ukládání a načítání scény do JSONu
  - JUnit 5 (`junit-*`, `opentest4j`, `apiguardian`) - jen pro testy

## Spuštění v IntelliJ IDEA

1. Otevřít složku projektu (File > Open). Modul je popsaný v `canvas-rasterization-java.iml`, ten už na knihovny v `lib/` odkazuje.
2. Nastavit SDK na JDK 23 (File > Project Structure > Project).
3. Spustit `src/Main.java`.

Testy: pravým na složku `test` > Run 'All Tests'.

## Spuštění z příkazové řádky (Windows)

Spouštět ze složky projektu:

```
javac -d out -cp "lib/*" -sourcepath src src/Main.java
java -cp "out;res;lib/*" Main
```

`-sourcepath` zařídí, že se přeloží všechno, co `Main` používá. Na Linuxu / macOS se v `-cp` místo `;` píše `:`.

## Struktura

- `src/` - zdrojáky
- `test/` - unit testy
- `res/` - resources, je tam demo scéna `scenes/demo.json` (načítá se přes File > Load demo scene), složka musí zůstat vedle `src`
- `lib/` - knihovny (Gson, JUnit)
- `doc/` - autoevaluační tabulky a vizualizace algoritmů
