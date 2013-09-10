package gui;

public class CloudMapThread<T> extends Thread {

    CloudMapPanel<T> cloudMap;
    boolean stop;

    public CloudMapThread (CloudMapPanel<T> cloudMap) {
        this.cloudMap = cloudMap;
        stop = false;
    }

    @Override
    public void run() {

        while (cloudMap.getNoMaps()<CloudMapPanel.sampleSize && !stop) {

            cloudMap.addMaps( 1 );
            try {
                Thread.sleep( 5 );
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (cloudMap.getNoPlaytestedSamples() < CloudMapPanel.sampleSize && !stop) {
            cloudMap.playtestSample();
        }
        
    }

    public void stopAdding() {
        stop = true;
        
    }
}