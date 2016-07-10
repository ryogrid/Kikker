package jp.ryo.informationPump.server.data;

public class SearchResult {
    public StoreBoxForWebData results[];
    public double similarities[];//resultsの各要素に対しての類似度

    public SearchResult(StoreBoxForWebData[] results, double[] similarities) {
        this.results = results;
        this.similarities = similarities;
    }

}
