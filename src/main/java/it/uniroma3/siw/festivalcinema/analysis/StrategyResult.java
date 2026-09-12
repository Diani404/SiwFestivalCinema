package it.uniroma3.siw.festivalcinema.analysis;

public record StrategyResult(String strategy, int loadedObjects, long sqlQueries, long millis) {

    public String format() {
        return String.format("Strategia: %-13s  oggetti caricati: %4d   query SQL: %4d   tempo: %4d ms",
                strategy, loadedObjects, sqlQueries, millis);
    }
}
