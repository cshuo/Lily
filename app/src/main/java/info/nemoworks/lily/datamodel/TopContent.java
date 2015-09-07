package info.nemoworks.lily.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class TopContent {

    public List<TopItem> getItems() {
        return items;
    }

    /**
     * An array of sample (dummy) items.
     */
    public List<TopItem> items = new ArrayList<TopItem>();

    public void addItem(TopItem item) {
        items.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TopItem {
        public int rank;
        public String content;
        public String url;

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl(){
            return url;
        }

        public void setUrl(String url){
            this.url = url;
        }

        public TopItem(int rank, String content,String url) {
            this.rank = rank;
            this.content = content;
            this.url = url;
        }

        @Override
        public String toString() {
            return content + "第" + (rank+1) + "名";
        }
    }
}

