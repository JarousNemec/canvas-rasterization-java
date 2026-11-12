# Canvas rasterization app

Projekt do PGRF1 - rasterizace úsečky a polygonu, vyplňování (seed fill, scan line, i vzorem) a ořezávání (Cohen-Sutherland, Sutherland-Hodgman).

## Co je potřeba

- **JDK 21** nebo novější (vyvíjeno na JDK 23)
- knihovny jsou přímo v projektu ve složce `lib/`, takže není potřeba Maven ani internet:
  - `gson-2.12.1.jar` - ukládání a načítání scény do JSONu
  - JUnit 5 (`junit-*`, `opentest4j`, `apiguardian`) - jen pro testy

## Spuštění v IntelliJ IDEA

1. Otevřít složku projektu (File > Open). Modul je popsaný v `canvas-rasterization-java.iml`, ten už na knihovny v `lib/` odkazuje.
2. Nastavit SDK na JDK 21 nebo novější (File > Project Structure > Project).
3. Spustit `src/Main.java`.

Testy: pravým na složku `test` > Run 'All Tests'.

## Spuštění z příkazové řádky (Windows)

Spouštět ze složky projektu:

```
javac -d out -cp "lib/*" -sourcepath src src/Main.java
java -cp "out;res;lib/*" Main
```

`-sourcepath` zařídí, že se přeloží všechno, co `Main` používá. Na Linuxu / macOS se v `-cp` místo `;` píše `:`.

## Ovládání

Režim se přepíná v liště (Mode). Lišta ukazuje jen tlačítka, která mají v daném režimu smysl.

- **C** - smaže celou scénu, vrátí ořezávače do výchozího stavu a vypne ořezy (funguje v každém režimu)
- **Line**
  - táhnutí levým - nová úsečka
  - Shift při táhnutí - přichycení na vodorovnou / svislou / úhlopříčnou (jde zmáčknout i během táhnutí)
  - pravé na úsečku - smazání
- **Polygon**
  - levé - přidá vrchol (táhnutím je vidět náhled)
  - **A** - ukončí polygon a začne nový
  - pravé na hranu - smaže celý polygon
  - vyplní se, když Fill color není černá (černá = pozadí = bez výplně) nebo je zvolený vzor
- **Vertex**
  - táhnutí levým za vrchol - posun vrcholu
  - táhnutí levým za hranu - vloží nový vrchol a rovnou ho táhne
  - pravé na vrchol - smazání vrcholu (polygonu musí zůstat aspoň 3)
- **Select**
  - levé na objekt - výběr, barvy a vzor objektu se načtou do lišty a změny v liště se na něm hned projeví
- **Fill**
  - levé do oblasti - seed fill
  - Seed fill mode: Background (omezení barvou pozadí) nebo Border (omezení barvou hranice = Line color)
- **Clip**
  - checkboxy Clip lines (Cohen-Sutherland), Clip polygons (Sutherland-Hodgman) a Show clippers
  - když jsou ořezávače zobrazené, jde táhnout za rohy okna a vrcholy polygonu, táhnutím za hranu polygonu se přidá vrchol, pravým se smaže (musí jich zůstat aspoň 5, polygon musí zůstat konvexní)

Vzory: ve Filler mode (Polygon, Fill, Select) jde zvolit Solid, Checkerboard, Stripes nebo Diagonal stripes. První barva je Fill color, druhá Pattern color.

Menu File: Open scene..., Save scene... a Load demo scene.

`SeedFillerRecursionImpl` se v aplikaci nepoužívá, je v projektu jen pro porovnání se zásobníkovou verzí `SeedFillerStackImpl` (rekurze na velkých plochách přeteče zásobník).

## Struktura

- `src/` - zdrojáky
- `test/` - unit testy
- `res/` - resources, je tam demo scéna `scenes/demo.json` (načítá se přes File > Load demo scene), složka musí zůstat vedle `src`
- `lib/` - knihovny (Gson, JUnit)
- `doc/` - autoevaluační tabulky a vizualizace algoritmů
