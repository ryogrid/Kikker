package jp.ryo.informationPump.server.data;

public class SearchResult {
    public StoreBoxForWebData results[];
    public double similarities[];//results�̊e�v�f�ɑ΂��Ă̗ގ��x

    public SearchResult(StoreBoxForWebData[] results, double[] similarities) {
        this.results = results;
        this.similarities = similarities;
    }

}