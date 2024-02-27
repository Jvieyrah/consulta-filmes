package com.betrybe.consultafilmes;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jdk.jfr.Category;

public class Queries {

  private final Collection<Movie> movies;

  public Queries(Collection<Movie> movies) {
    this.movies = movies;
  }

  /**
   * Consulta 1: Retorne uma lista com os filmes lançados em um ano específico.
   */
  public List<Movie> moviesByGivenYear(int ano) {

    Optional<List<Movie>> moviesByGivenYear = Optional.of(movies.stream()
        .filter(movie -> movie.releaseYear == ano)
        .toList());

    return moviesByGivenYear.orElse(emptyList());

  }

  /**
   * Consulta 2: Retorna uma lista ordenada com os nomes de todos os atores e atrizes.
   */
  public List<String> allActorsInAlphabeticalOrder() {
    return movies.stream().flatMap(actor -> actor.getActors().stream())
        .distinct().sorted().toList();
  }

  /**
   * Consulta 3: Retorna um Map que tem como chave os atores ou atrizes e como valor um conjunto com
   * os filmes que eles interpretaram.
   */
  public Map<String, Set<String>> selectMoviesByActor(String ator) {
    return movies.stream()
        .filter(actor -> actor.getActors().contains(ator))
        .collect(Collectors.groupingBy(
            movie -> ator,
            Collectors.mapping(Movie::getTitle, Collectors.toSet())));
  }

  /**
   * Consulta 4: a partir da coleção de filmes desta classe, este método retorna o conjunto de
   * atores que interpretaram a si próprios em pelo menos um desses filmes.
   *
   * <p>Considera-se "atores que interpretaram a si próprios" aqueles que têm o seu nome como
   * uma das chaves do Map `atoresPorPersonagem` e também como um dos itens pertencentes ao conjunto
   * associado a esta mesma chave.</p>
   */
  public Set<String> actorsThatInterpretThemselves() {
    return movies.stream().flatMap(movie -> movie.actorsByCharacters.entrySet().stream())
        .filter(entry -> entry.getValue().contains(entry.getKey()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }


  /**
   * Consulta 5: a partir da coleção de filmes desta classe, este método retorna a lista de atores
   * que atuaram em pelo menos um filme de um determinado diretor. A lista retornada está disposta
   * em ordem alfabética.
   *
   * <p>Considera-se que um ator tenha atuado em um filme de um determinado diretor se ele tem o
   * seu nome como um dos itens do campo `atores`, ao mesmo tempo em que o diretor em questão tem o
   * seu nome como um dos itens do campo `directors` do mesmo filme.</p>
   */
  public List<String> actorsThatActedInMoviesOfDirectorInAlphabeticOrder(String diretor) {
    return movies.stream().filter(movie -> movie.getDirectors().contains(diretor))
            .flatMap(movie -> movie.getActors().stream()).distinct().sorted()
        .collect(Collectors.toList());
  }

  /**
   * Consulta 6: a partir da coleção de filmes desta classe, este método retorna a lista de filmes
   * em que pelo menos um dos directors tenha atuado. A lista retornada está disposta em ordem de
   * lançamento, com os filmes mais recentes no início.
   *
   * <p>Considera-se "filmes em que pelo menos um dos directors tenha atuado" aqueles em que
   * pelo menos um dos itens do campo `directors` também é um item do campo `atores`.</p>
   */
  public List<Movie> moviesWithAtLeastOneDirectorActedMostRecentFirst() {
    return movies.stream()
            .filter(movie -> movie.getDirectors().stream().anyMatch(
                director -> movie.getActors().contains(director)))
        .distinct().sorted(Comparator.comparingInt((Movie movie) -> movie.releaseYear).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Consulta 7: a partir da coleção de filmes desta classe, este método retorna um Map contendo all
   * os filmes lançados em um determinado ano agrupados por categoria.
   *
   * <p>Cada chave do Map representa uma categoria, enquanto cada valor representa o
   * conjunto de filmes que se encaixam na categoria da chave correspondente.</p>
   */
  public Map<String, Set<Movie>> moviesReleasedInYearGroupedByCategory(int ano) {
    return movies.stream().filter(movie -> movie.getReleaseYear() == ano)
            .flatMap(movie -> movie.getCategories().stream()
                    .map(category -> Map.entry(category, movie)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
  }
}
