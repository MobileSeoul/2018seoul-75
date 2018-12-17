package com.android.jakchang.foodtruck;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {


        private static Map<String, Object> mDataHolder = new ConcurrentHashMap<>();

        public static String putDataHolder(Object data){
            //중복되지 않는 홀더 아이디를 생성해서 요청자에게 돌려준다.
            String dataHolderId = UUID.randomUUID().toString();
            mDataHolder.put(dataHolderId, data);
            return dataHolderId;
        }



        public static Object popDataHolder(String key){
            Object obj = mDataHolder.get(key);
            //pop된 데이터는 홀더를 제거
            mDataHolder.remove(key);
            return obj;
        }

}
