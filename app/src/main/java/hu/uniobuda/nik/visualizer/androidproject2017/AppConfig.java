package hu.uniobuda.nik.visualizer.androidproject2017;

public class AppConfig {
    // Server user login url
    //public static String URL_LOGIN = "http://192.168.0.4:8555/api/login";
    // Server user register url
    //public static String URL_REGISTER = "http://192.168.0.4:8555/api/register";

    //Login URL: <host>/api/login
    public static String URL_LOGIN = "http://repoanalytics.azurewebsites.net/api/user/loginuser";
    //Register URL: <host>/api/register
    public static String URL_REGISTER = "http://repoanalytics.azurewebsites.net/api/user/registeruser";
    //Updates URL: <host>/api/updates
    public static String URL_UPDATES = "";
    //Add repo URL: <host>/api/repos/add
    public static String URL_REPO_ADD = "";
    //List repos URL: <host>/api/repos/list
    public static String URL_REPO_LIST = "";
    //Stat of repo URL: <host>/api/repos/stat
    public static String URL_REPO_STAT = "http://repoanalytics.azurewebsites.net/api/git/repostat";
    //Commits of repo URL: <host>/api/repos/commits
    public static String URL_REPO_COMMITS = "";

}
