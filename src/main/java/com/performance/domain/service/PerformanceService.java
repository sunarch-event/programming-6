package com.performance.domain.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.performance.domain.dao.UserDao;
import com.performance.domain.entity.UserHobby;
import com.performance.domain.entity.UserInfo;
import com.performance.domain.entity.UserMaster;

@Service
public class PerformanceService {

    final static Logger log = LoggerFactory.getLogger(PerformanceService.class);

    private final String MEASURE_FLAG_ON  = "1";
    
    // メソッドを跨いで使用する定数
    private static final String SPREADSHEET_EROOR = "スプレッドシートの更新でエラーが発生しました。";
    private static final String CSV_READ_ERROR = "csv read error";
    private static final String COMMA = ",";
    private static final String LOG_BOUNDARY = "-------------------------------";
    private static final String DATA_READ = "データ読み込み";
    private static final String ITEM = "件目"; 
    private static final String LAST_NAME = "ユーザー姓:";
    private static final String PREFECTURES = "出身都道府県:";
    private static final String FIRST_NAME = "ユーザー名:";
    private static final String CITY = "出身市区町村:";
    private static final String BLOODTYPE = "血液型:";
    private static final String HOBBY_1 = "趣味1:";
    private static final String HOBBY_2 = "趣味2:";
    private static final String HOBBY_3 = "趣味3:";
    private static final String HOBBY_4 = "趣味4:";
    private static final String HOBBY_5 = "趣味5:";
    private static final String TARGET_ITEMS = ".新潟県,上越市.";
    private static final String DATA_WRITING = "データ書き込み";

    private static final String CSV_USERINFO = "data/userInfo.csv";
    private static final String CSV_ASSERTIONDATA = "data/assertionData.csv";

    private GoogleApiService googleService;

    private UserDao userDao;

    // CSV取得用リスト
    private List<String> csvFile = new ArrayList<String>();
    
    private Map<String, Long> resultMap = new HashMap<String, Long>();
    private Map<String, Boolean> assertionResultMap = new HashMap<String, Boolean>();

    public PerformanceService(GoogleApiService googleService, UserDao userDao) {
        this.googleService = googleService;
        this.userDao = userDao;
    }

    @Async("perfomanceExecutor")
    public void execute(String uuid, String measureFlag) {

        resultMap.clear();
        resultMap.put(uuid, null);

        Long start = System.currentTimeMillis();

        List<UserMaster> matchingUserList = uploadExecute();

        Long end = System.currentTimeMillis();
        Long executeTime = end - start;

        resultMap.put(uuid, executeTime);
        // アサーション入れる
        Boolean assertionResult = assertion(matchingUserList);
        assertionResultMap.put(uuid, assertionResult);
        
        // 計測実施かつアサーションが成功している場合のみ送る
        if(MEASURE_FLAG_ON.equals(measureFlag) && assertionResult) {
            try {
                googleService.execute(executeTime);
            } catch (Exception e) {
                log.error(SPREADSHEET_EROOR, e);
            }
        }
        return;
    }
    public List<UserMaster> uploadExecute() {
        // テーブル情報を空にする
        /** 変更不可 **/
        truncateTable();
        /** 変更不可 **/
        
        // CSVを取得・CSVファイルをDBに登録する
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CSV_USERINFO), StandardCharsets.UTF_8));){

            //読み込み行
            String readLine;

            //読み込み行数の管理
            int i = 0;

            //1行ずつ読み込みを行う
            while ((readLine = br.readLine()) != null) {
                i++;
                //データ内容をコンソールに表示する
                log.info(LOG_BOUNDARY);

                //データ件数を表示
                log.info(DATA_READ + i + ITEM);
                
                csvFile.add(readLine);
            }
        } catch (Exception e) {
            log.info(CSV_READ_ERROR, e);
        }

        try {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for(String line : csvFile) {
                //カンマで分割した内容を配列に格納する
                String[] data = line.split(COMMA, -1);
                
                //データ内容をコンソールに表示する
                log.info(LOG_BOUNDARY);
                //データ件数を表示
                //配列の中身を順位表示する。列数(=列名を格納した配列の要素数)分繰り返す
                log.debug(sb.append(LAST_NAME).append(data[1]).append(COMMA)
                            .append(PREFECTURES).append(data[2]).append(COMMA)
                            .append(FIRST_NAME).append(data[0]).append(COMMA)
                            .append(CITY).append(data[3]).append(COMMA)
                            .append(BLOODTYPE).append(data[4]).append(COMMA)
                            .append(HOBBY_1)).append(data[5]).append(COMMA)
                            .append(HOBBY_2).append(data[6]).append(COMMA)
                            .append(HOBBY_3).append(data[7]).append(COMMA)
                            .append(HOBBY_4).append(data[8]).append(COMMA)
                            .append(HOBBY_5).append(data[9]).toString());
                
                            sb.setLength(0);

                UserInfo userInfo = new UserInfo();
                UserHobby userHobby = new UserHobby();

                userInfo.setLastName(data[0]);
                userInfo.setFirstName(data[1]);
                userInfo.setPrefectures(data[2]);
                userInfo.setCity(data[3]);
                userInfo.setBloodType(data[4]);
                userHobby.setHobby1(data[5]);
                userHobby.setHobby2(data[6]);
                userHobby.setHobby3(data[7]);
                userHobby.setHobby4(data[8]);
                userHobby.setHobby5(data[9]);
                // 特定の件のみインサートするようにする
                Matcher matcher = Pattern.compile(TARGET_ITEMS).matcher(line);
                if(matcher.find()) {
                    // 行数のインクリメント
                    i++;
                    log.info(DATA_WRITING + i + ITEM);
                    userDao.insertUserInfo(userInfo);
                    Long id = userDao.selectId(userInfo);
                    userHobby.setId(id);
                    userDao.insertUserHobby(userHobby);
                }
            }
            //CSV取得用リストのクリア
            csvFile.clear();

        } catch (Exception e) {
            log.info(CSV_READ_ERROR, e);
        }
        // 対象情報取得
        UserInfo targetUserInfo = userDao.getTargetUserInfo();
        UserHobby targetUserHobby = userDao.getTargetUserHobby(targetUserInfo);
        UserMaster targetUserMaster = new UserMaster();
        
        targetUserMaster.setId(targetUserInfo.getId());
        targetUserMaster.setLastName(targetUserInfo.getLastName());
        targetUserMaster.setFirstName(targetUserInfo.getFirstName());
        targetUserMaster.setPrefectures(targetUserInfo.getPrefectures());
        targetUserMaster.setCity(targetUserInfo.getCity());
        targetUserMaster.setBloodType(targetUserInfo.getBloodType());
        targetUserMaster.setHobby1(targetUserHobby.getHobby1());
        targetUserMaster.setHobby2(targetUserHobby.getHobby2());
        targetUserMaster.setHobby3(targetUserHobby.getHobby3());
        targetUserMaster.setHobby4(targetUserHobby.getHobby4());
        targetUserMaster.setHobby5(targetUserHobby.getHobby5());

        // DBから検索する
        List<UserInfo> userInfoList = userDao.searchUserInfo();
        List<UserHobby> userHobbyList = userDao.searchUserHobby(targetUserHobby);
        
        List<UserMaster> userMasterList = new ArrayList<UserMaster>();
        
        for(int i = 0,iLen = userInfoList.size(); i < iLen; i++) {
            UserMaster userMaster = new UserMaster();
            userMaster.setId(userInfoList.get(i).getId());
            userMaster.setLastName(userInfoList.get(i).getLastName());
            userMaster.setFirstName(userInfoList.get(i).getFirstName());
            userMaster.setPrefectures(userInfoList.get(i).getPrefectures());
            userMaster.setCity(userInfoList.get(i).getCity());
            userMaster.setBloodType(userInfoList.get(i).getBloodType());
            for(int j = 0,jLen = userHobbyList.size(); j < jLen; j++) {
                if(userMaster.getId().equals(userHobbyList.get(j).getId())) {
                    userMaster.setHobby1(userHobbyList.get(j).getHobby1());
                    userMaster.setHobby2(userHobbyList.get(j).getHobby2());
                    userMaster.setHobby3(userHobbyList.get(j).getHobby3());
                    userMaster.setHobby4(userHobbyList.get(j).getHobby4());
                    userMaster.setHobby5(userHobbyList.get(j).getHobby5());
                    break;
                }
            }
            userMasterList.add(userMaster);
        }
        
        List<UserMaster> bloodMatchingUserList = new ArrayList<UserMaster>();
        // 同じ血液型ユーザー
        for(UserMaster user : userMasterList) {
            if(user.getBloodType().equals(targetUserMaster.getBloodType())) {
                bloodMatchingUserList.add(user);
            }
        }
        List<UserMaster> matchingUserList = new ArrayList<UserMaster>();
        // 趣味1から5のいずれかに同じ趣味を持っているユーザー
        for(UserMaster user : bloodMatchingUserList) {
            // 趣味1に同じ趣味を持っているユーザー
            if(user.getHobby1().equals(targetUserMaster.getHobby1()) || user.getHobby1().equals(targetUserMaster.getHobby2()) || user.getHobby1().equals(targetUserMaster.getHobby3()) || user.getHobby1().equals(targetUserMaster.getHobby4()) || user.getHobby1().equals(targetUserMaster.getHobby5())) {
                if(!matchingUserList.contains(user)) {
                    matchingUserList.add(user);
                }
            }
            // 趣味2に同じ趣味を持っているユーザー
            if(user.getHobby2().equals(targetUserMaster.getHobby1()) || user.getHobby2().equals(targetUserMaster.getHobby2()) || user.getHobby2().equals(targetUserMaster.getHobby3()) || user.getHobby2().equals(targetUserMaster.getHobby4()) || user.getHobby2().equals(targetUserMaster.getHobby5())) {
                if(!matchingUserList.contains(user)) {
                    matchingUserList.add(user);
                }
            }
            // 趣味3に同じ趣味を持っているユーザー
            if(user.getHobby3().equals(targetUserMaster.getHobby1()) || user.getHobby3().equals(targetUserMaster.getHobby2()) || user.getHobby3().equals(targetUserMaster.getHobby3()) || user.getHobby3().equals(targetUserMaster.getHobby4()) || user.getHobby3().equals(targetUserMaster.getHobby5())) {
                if(!matchingUserList.contains(user)) {
                    matchingUserList.add(user);
                }
            }
            // 趣味4に同じ趣味を持っているユーザー
            if(user.getHobby4().equals(targetUserMaster.getHobby1()) || user.getHobby4().equals(targetUserMaster.getHobby2()) || user.getHobby4().equals(targetUserMaster.getHobby3()) || user.getHobby4().equals(targetUserMaster.getHobby4()) || user.getHobby4().equals(targetUserMaster.getHobby5())) {
                if(!matchingUserList.contains(user)) {
                    matchingUserList.add(user);
                }
            }
            // 趣味5に同じ趣味を持っているユーザー
            if(user.getHobby5().equals(targetUserMaster.getHobby1()) || user.getHobby5().equals(targetUserMaster.getHobby2()) || user.getHobby5().equals(targetUserMaster.getHobby3()) || user.getHobby5().equals(targetUserMaster.getHobby4()) || user.getHobby5().equals(targetUserMaster.getHobby5())) {
                if(!matchingUserList.contains(user)) {
                    matchingUserList.add(user);
                }
            }


        }
        
        return matchingUserList;
    }

    
    public void truncateTable() {
        userDao.truncateUserInfo();
        userDao.truncateUserHobby();
    }

    public Long referenceExecuteTime(String uuid) {
        
        Long result = null;
        if(resultMap.containsKey(uuid)) {
            result = resultMap.get(uuid);
        }
        
        return result;
    }
    
    public String referenceUuid() {
        
        String uuid = null;
        
        for(String key : resultMap.keySet()) {
            uuid = key;
        }
        
        return uuid;
    }

    private Boolean assertion(List<UserMaster> matchingUserList) {
        Boolean assertionResult = true;
        
        int count = userDao.searchCount();
        
        if(count != 10000) {
            return false;
        }
        
        if(matchingUserList.size() != 2072) {
            return false;
        }
        
        // CSVを取得・CSVファイルをDBに登録する
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CSV_ASSERTIONDATA), StandardCharsets.UTF_8));){
            //読み込み行
            String readLine;
            //1行ずつ読み込みを行う
            while ((readLine = br.readLine()) != null) {
                csvFile.add(readLine);
            }
        } catch (Exception e) {
            log.info(CSV_READ_ERROR, e);
        }
        for(String line : csvFile) {
            boolean exsits = false;
            String[] data = line.split(COMMA, -1);

            UserMaster userMaster = new UserMaster();
            userMaster.setLastName(data[0]);
            userMaster.setFirstName(data[1]);
            userMaster.setPrefectures(data[2]);
            userMaster.setCity(data[3]);
            userMaster.setBloodType(data[4]);
            userMaster.setHobby1(data[5]);
            userMaster.setHobby2(data[6]);
            userMaster.setHobby3(data[7]);
            userMaster.setHobby4(data[8]);
            userMaster.setHobby5(data[9]);
            for(UserMaster user : matchingUserList) {
                if(user.toString().equals(userMaster.toString())) {
                    exsits = true;
                    break;
                }
            }
            if(!exsits) {
                assertionResult = false;
            }
        }
        //CSV取得用リストのクリア
        csvFile.clear();
        truncateTable();
        return assertionResult;
    }

    public Boolean referenceAssertionResult(String uuid) {
        Boolean assertionResult = assertionResultMap.get(uuid);
        return assertionResult;
    }
}
