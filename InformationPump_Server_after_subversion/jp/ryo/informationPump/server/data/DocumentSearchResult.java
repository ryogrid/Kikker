package jp.ryo.informationPump.server.data;


public class DocumentSearchResult {
    public DocumentEntry results[];
    public double similarities[];//results�̊e�v�f�ɑ΂��Ă̗ގ��x
    public double eval_points[]; //�ŏI�I�ȕ]���l
    
    public DocumentSearchResult(DocumentEntry[] results, double[] similarities,double[] eval_points) {
        this.results = results;
        this.similarities = similarities;
        this.eval_points = eval_points;
    }
}