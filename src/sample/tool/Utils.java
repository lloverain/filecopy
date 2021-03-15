package sample.tool;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import sample.outlog.ProgrammerLog;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ProgrammerLog programmerLog = new ProgrammerLog();

    /**
     * 读取文件
     *
     * @return
     */
    public static String read_file(String filename) {
        String str = "";
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
            bufferedReader = new BufferedReader(fileReader); //相对文件路径，如果是放在项目文件夹下，则为new FileReader("test.txt");
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                str += string;
            }

        } catch (IOException e) {
            programmerLog.error("读取文件<<" + filename + ">>失败", e);
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                programmerLog.error("读取文件<<" + filename + ">>关闭流失败", e);
            }
        }
        return str;
    }


    public static String batchReadFile(List<String> fileList) {
        String text = "";
        for (String file : fileList) {
            text += read_file("log/" + file);
        }
        return text;
    }


    /**
     * 指定文件写入数据
     *
     * @param name
     * @param data
     */
    public static void writer_file(String name, String data) {
        File file = new File(name);
        if (file.exists()) {
            file.delete();
        }
        Writer out = null;
        try {
            out = new FileWriter(file);
            out.write(data);
            out.close();
            file.setReadOnly();
        } catch (IOException e) {
            programmerLog.error("写入文件<<" + name + ">>失败", e);
        }
    }

    /**
     * 获取时间格式 danjiaxin
     * yyyyMMdd HH:mm:ss
     */
    public static String getDateTime() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date());
    }


    /**
     * get提交
     *
     * @param url
     * @param paramMap
     * @param headerMap
     * @return
     */
    public static String get(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        String ret = null;
        HttpClient httpclient = new HttpClient();
        GetMethod getMethod = null;
        try {
            getMethod = new GetMethod(url);
            // 封装请求头参数
            if (null != headerMap) {
                Set<String> headerNames = headerMap.keySet();
                for (String headerName : headerNames) {
                    String headerValue = headerMap.get(headerName);
                    getMethod.addRequestHeader(headerName, headerValue);
                }
            }
            // 封装请求参数
            String queryString = "";
            if (null != paramMap) {
                Set<String> keys = paramMap.keySet();
                for (String key : keys) {
                    String value = paramMap.get(key);
                    queryString += "&" + key + "=" + value;
                }
            }
            if (StringUtils.isNotBlank(queryString)) {
                queryString = queryString.substring(1);
                getMethod.setQueryString(URIUtil.encodeQuery(queryString, "UTF-8"));
            }
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            int statusCode = httpclient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + getMethod.getStatusLine());
            }
            byte[] resp = getMethod.getResponseBody();
            ret = new String(resp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * post 提交
     *
     * @param url
     * @param requestBody
     * @param contentType
     * @param headerMap
     * @return
     */
    public static String post(String url, String requestBody, String contentType, Map<String, String> headerMap) {
        String ret = null;
        HttpClient httpclient = new HttpClient();
        PostMethod postMethod = null;
        try {
            // 创建post方法
            postMethod = new PostMethod(url);
            // 封装请求头参数
            if (null != headerMap) {
                Set<String> headerNames = headerMap.keySet();
                for (String headerName : headerNames) {
                    String headerValue = headerMap.get(headerName);
                    postMethod.addRequestHeader(headerName, headerValue);
                }
            }
            // 封装请求参数
            RequestEntity requestEntity = new StringRequestEntity(requestBody, contentType, "utf-8");

            postMethod.setRequestEntity(requestEntity);

            // 设置参数请求的编码
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            int statusCode = httpclient.executeMethod(postMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + postMethod.getStatusLine());
            }
            InputStream resp = postMethod.getResponseBodyAsStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(resp));
            ret = bf.readLine();
        } catch (IOException e) {
            programmerLog.error("post请求失败", e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
        return ret;
    }


    /**
     * 生成时间
     *
     * @return
     */
    public static String timeConversion() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(date);
        return time;
    }

    /**
     * 以分号;分隔
     * @param names
     * @return
     */
    public static List<String> getNameList(String names){
        String[] arr = new String(names).split("[\\;]");
        List<String> stringList = Arrays.asList(arr.clone());
        return stringList;
    }

    /**
     * 生成url
     * @param url
     * @return
     */
    public static String getUrl(String url){
        StringBuffer strB = new StringBuffer();
        String[] strArray = url.split("/");
        for (int i = 0; i < strArray.length; i++) {
            if(i!=(strArray.length-1)){
                strB.append(strArray[i]+"/");
            }
        }
        return strB.toString();
    }

    /**
     * 替换url
     * @param url
     * @return
     */
    public static String replaceUrl(String url,int num,String content){
        StringBuffer strB = new StringBuffer();
        String[] strArray = url.split("/");
        for (int i = 0; i < strArray.length; i++) {
            if(i==num){
                strArray[i]=content;
            }
            if(i!=(strArray.length-1)){
                strB.append(strArray[i]+"\\");
            }

        }
        return strB.toString();
    }

    /**
     * 获取文件名
     * @param url
     * @return
     */
    public static String getName(String url){
        StringBuffer strB = new StringBuffer();
        String[] strArray = url.split("/");
        for (int i = 0; i < strArray.length; i++) {
            if(i==(strArray.length-1)){
                strB.append(strArray[i]);
            }

        }
        return strB.toString();
    }
}
