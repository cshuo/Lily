package info.nemoworks.lily;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import info.nemoworks.lily.datamodel.TopContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TopFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private String[] Top10Url = new String[10];
    private TopContent tops;

    // TODO: Rename and change types of parameters
    public static TopFragment newInstance() {
        TopFragment fragment = new TopFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TopFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topitem, container, false);


        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.topitem_list);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        new DownloadTask().execute("http://bbs.nju.edu.cn/bbstop10");

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//           } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (null != mListener) {
//             Notify the active callbacks interface (the activity, if the
//             fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(TopContent.items.get(position).toString());
//        }
        String url = tops.getItems().get(position).getUrl();

        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame,fragment).addToBackStack(null);
        ft.commit();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    private LinkedHashMap<String,String> parseTop10() throws ParserException {
        final String DW_HOME_PAGE_URL = "http://bbs.nju.edu.cn/bbstop10";
//        ArrayList<String> pTitleList = new ArrayList<String>();
        // 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
        Parser htmlParser = new Parser(DW_HOME_PAGE_URL);
        htmlParser.setEncoding("UTF-8");
        LinkedHashMap<String,String> Top10Map = new LinkedHashMap<String,String>();

        String postTitle = "";
        String url = "";
        // 获取指定的 div 节点，即 <div> 标签，并且该标签包含有属性 id 值为“tab1”
        NodeList toptable = htmlParser.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableTag.class),
                new HasAttributeFilter("width", "640")));

        if (toptable != null && toptable.size() > 0) {
            // 获取指定 div 标签的子节点中的 <li> 节点
            NodeList itemTopList = toptable
                    .elementAt(0)
                    .getChildren()
                    .extractAllNodesThatMatch(
                            (new NodeClassFilter(TableRow.class)), true);

            if (itemTopList != null && itemTopList.size() > 0) {
                for (int i = 1; i < itemTopList.size(); ++i) {
                    // 在 <li> 节点的子节点中获取 Link 节点
                    NodeList linkItem = itemTopList.elementAt(i).getChildren()
                            .extractAllNodesThatMatch(new TagNameFilter("td"));
                    if (linkItem != null && linkItem.size() > 0) {
                        // 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
                        postTitle = ((LinkTag) ((linkItem.elementAt(2)).getChildren().elementAt(0))).getLinkText();
                        url = ((LinkTag) ((linkItem.elementAt(2)).getChildren().elementAt(0))).getLink();
//                        System.out.println(url);
//                        pTitleList.add(postTitle);
                        Top10Map.put(postTitle,url);
                    }
                }
            }
        }
//        String[] results = new String[pTitleList.size()];
//        pTitleList.toArray(results);
//        return results;
        return Top10Map;
    }

    private class DownloadTask extends AsyncTask<String, Void, LinkedHashMap> {


        @Override
        protected LinkedHashMap<String,String> doInBackground(String... urls) {
            try {
                return parseTop10();
            } catch (ParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(LinkedHashMap results) {
            tops = new TopContent();
            Iterator item = results.entrySet().iterator();

            int i = 0;
            while (item.hasNext()){
                Map.Entry entry = (Map.Entry)item.next();
                tops.addItem(new TopContent.TopItem(i, entry.getKey().toString(), entry.getValue().toString()));
                Top10Url[i] = entry.getValue().toString();
                i++;
            }

            // TODO: Change Adapter to display your content
            mAdapter = new ArrayAdapter<TopContent.TopItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, tops.getItems());

            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
    }

    /**
     * Initiates the fetch operation.
     */
//    private String loadFromNetwork(String urlString) throws IOException {
//        InputStream stream = null;
//        String str = "";
//
//        try {
//            stream = downloadUrl(urlString);
//            str = readIt(stream, 5000);
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
//        return str;
//    }

//    private InputStream downloadUrl(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000 /* milliseconds */);
//        conn.setConnectTimeout(15000 /* milliseconds */);
//        conn.setRequestMethod("GET");
//        conn.setDoInput(true);
//        // Start the query
//        conn.connect();
//        InputStream stream = conn.getInputStream();
//        return stream;
//    }

//    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
//
//        StringBuilder out = new StringBuilder();
//
//        Reader reader = null;
//        reader = new InputStreamReader(stream, "UTF-8");
//        char[] buffer = new char[500];
//
//        try {
//            for (; ; ) {
//                int rsz = reader.read(buffer, 0, buffer.length);
//                if (rsz < 0)
//                    break;
//                out.append(buffer, 0, rsz);
//            }
//        } finally {
//            reader.close();
//        }
//
//        return out.toString();
//    }

}
