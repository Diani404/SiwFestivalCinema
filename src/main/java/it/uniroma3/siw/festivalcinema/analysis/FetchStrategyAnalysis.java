package it.uniroma3.siw.festivalcinema.analysis;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.festivalcinema.model.Festival;
import it.uniroma3.siw.festivalcinema.repository.FestivalRepository;

@Component
@Profile("analysis")
public class FetchStrategyAnalysis implements CommandLineRunner {

    private final FetchStrategyAnalysisService analysisService;
    private final FestivalRepository festivalRepository;

    public FetchStrategyAnalysis(FetchStrategyAnalysisService analysisService, FestivalRepository festivalRepository) {
        this.analysisService = analysisService;
        this.festivalRepository = festivalRepository;
    }

    @Override
    public void run(String... args) {
        List<Festival> festivals = festivalRepository.findAllByOrderByStartDateDesc();
        if (festivals.isEmpty()) {
            System.out.println("Nessun festival nel database: analisi non eseguita");
            return;
        }
        Long festivalId = festivals.get(0).getId();

        //prima a vuoto per creare cache di hibernate
        analysisService.moviesJoinFetch(festivalId);
        analysisService.screeningsJoinFetch(festivalId);

        System.out.println();
        System.out.println("Analisi - festival id: " + festivalId
                + " - " + festivals.get(0).getName());
        System.out.println();
        System.out.println("Query 1: film del festival con i relativi registi");
        print(List.of(
                analysisService.moviesLazy(festivalId),
                analysisService.moviesJoinFetch(festivalId),
                analysisService.moviesEntityGraph(festivalId)));
        System.out.println();
        System.out.println("Query 2: programma del festival (proiezioni con film, regista e sala)");
        print(List.of(
                analysisService.screeningsLazy(festivalId),
                analysisService.screeningsJoinFetch(festivalId)));
        System.out.println();
    }

    private void print(List<StrategyResult> results) {
        for (StrategyResult result : results) {
            System.out.println(result.format());
        }
    }
}
