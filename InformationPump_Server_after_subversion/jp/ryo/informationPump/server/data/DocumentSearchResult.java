package jp.ryo.informationPump.server.data;


public class DocumentSearchResult {
    public DocumentEntry results[];
    public double similarities[];//resultsの各要素に対しての類似度
    public double eval_points[]; //最終的な評価値
    
    public DocumentSearchResult(DocumentEntry[] results, double[] similarities,double[] eval_points) {
        this.results = results;
        this.similarities = similarities;
        this.eval_points = eval_points;
    }
}
