# Durag + Damian – mod do Minecraft (Fabric 1.21.1)

Dodaje przedmiot **Durag** – czarną satynową chustę na głowę.

- Zakładasz go prawym kliknięciem albo wkładasz w slot hełmu.
- Receptura: 3x czarna wełna (góra) + 2x nić (dół, po bokach).
- Jest w kreatywnej zakładce "Walka".
- Czysto kosmetyczny (0 pancerza).

## Damian (NPC)
- Wywołanie: jajo przyzywające w zakładce kreatywnej „Jajka przyzywające” albo `/summon durag:damian`.
- Z duragiem na głowie spokojnie chodzi i co 5–15 s pisze na czacie `<Damian> Damian grr`.
- **Prawy klik pustą ręką** zdejmuje mu durag (trafia do Twojego ekwipunku) – wtedy Damian goni i bije gracza.
- **Prawy klik duragiem** na wściekłego Damiana zakłada mu durag z powrotem i go uspokaja.
- Dźwięki (zapętlone, podążają za Damianem): normalnie „Halo to ja, Twój telefon”, podczas pościgu „Tiki Tiki”.
- Tekstura Damiana (`textures/entity/damian.png`) ma 1024x1024 (16x większa niż standardowe 64x64) – na przodzie głowy jest zdjęcie twarzy. Układ UV jest ten sam co w skinie 64x64, więc można ją podmienić na własną o rozdzielczości 64x64, 128x128, 256x256 itd.
- Na poziomie trudności Spokojny obrażenia od mobów są zerowe (zasada gry).

## Stasiek (Kamehameha)
- Wywołanie: jajo przyzywające „Stasiek” (zakładka „Jajka przyzywające”) albo `/summon durag:kame_npc`.
- Ma na skinie wklejoną twarz ze zdjęcia (`textures/entity/kame_npc.png`, 1024x1024, układ UV jak w skinie 64x64) i pomarańczowe gi.
- Spokojnie chodzi. **Gdy go uderzysz**: gra dźwięk `kamehameha.ogg`, unosi ręce, ładuje niebieską kulę (ok. 8 s), po czym strzela promieniem Kamehameha (ok. 5 s) w Twoją stronę – 10 obrażeń (5 serc) + odrzut, każdy trafiony raz na strzał.
- Czasy i obrażenia zmienisz na górze pliku `KameEntity.java` (CHARGE_TICKS, BEAM_TICKS, BEAM_DAMAGE...).
- Na poziomie trudności Spokojny obrażenia od mobów są zerowe (zasada gry).

## Budowanie
Potrzebujesz JDK 21.
1. Skopiuj folder `gradle/` oraz pliki `gradlew`, `gradlew.bat` z https://github.com/FabricMC/fabric-example-mod
   (albo uruchom `gradle wrapper`, jeśli masz zainstalowanego Gradle).
2. `./gradlew build`
3. Gotowy mod: `build/libs/durag-1.0.0.jar` -> wrzuć do `.minecraft/mods` razem z Fabric API.

Do testów: `./gradlew runClient`

## Najprościej: zbuduj .jar na GitHubie (bez instalowania czegokolwiek)
1. Utwórz nowe repozytorium na github.com i wrzuć do niego zawartość tego folderu (razem z `.github`).
2. Wejdź w zakładkę **Actions** -> workflow **build** -> poczekaj ~2-3 min.
3. W podsumowaniu przebiegu pobierz artefakt **durag-jar** (w środku `durag-1.0.0.jar`).
