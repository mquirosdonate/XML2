package HTTPAsync;
import com.loopj.android.http.*;

/**
 * Created by Asus on 01/05/2017.
 */

public class HTTPAsyncLoopj {
    private static final String BASE_URL = "";

    public  AsyncHttpClient client = new AsyncHttpClient();


    public  void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public  void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private  String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
