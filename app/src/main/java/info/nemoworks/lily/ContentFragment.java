package info.nemoworks.lily;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import info.nemoworks.lily.datamodel.DividerItemDecoration;
import info.nemoworks.lily.datamodel.MyAdapter;
import info.nemoworks.lily.datamodel.SpacesItemDecoration;

/**
 * Created by cshuo on 15/9/6.
 */
public class ContentFragment extends Fragment{

    private RecyclerView mListView;

    private MyAdapter mAdapter;

    public ContentFragment(){

    }


    public static ContentFragment newInstance(String url){
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_content,container,false);

        mListView = (RecyclerView)view.findViewById(R.id.item_content);
        mListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        String url = (String) bundle.get("url");
        new DownloadTask().execute(url);
        return view;
    }

    private String[] parseContent(String url) throws ParserException {
        Parser parser = new Parser(url);
        parser.setEncoding("UTF-8");
        String[] contents = null;
        NodeList contentTable = parser.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableTag.class),
                new HasAttributeFilter("width","610")));

        if(contentTable != null && contentTable.size() > 0){
            contents = new String[contentTable.size()];
            for(int i=0;i<contentTable.size();i++){
                NodeList itemList = contentTable.elementAt(i).getChildren().
                        extractAllNodesThatMatch(new NodeClassFilter(TableRow.class),true);
                if(itemList != null && itemList.size() > 1){
                    contents[i] = itemList.elementAt(1).toPlainTextString().trim();
                }

            }
        }
        return contents;
    }

    private class DownloadTask extends AsyncTask<String,Void,String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            try {
                return parseContent(params[0]);
            } catch (ParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mAdapter = new MyAdapter(strings);
            mListView.setAdapter(mAdapter);
        }
    }


}


