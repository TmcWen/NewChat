package com.example.newchat.db;

import android.net.Uri;
import android.util.Log;

import com.example.newchat.R;
import com.example.newchat.chat.unit.Msg;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.mainmenu.unit.Chat;
import com.example.newchat.mainmenu.unit.Friend;
import com.example.newchat.unit.Ssh;
import com.example.newchat.unit.User;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class MysqlDatabase {
    //executeUpdate 记得用这个方法执行查询,删除

    public int deleteFriend(String id) {
        Connection c = getC();
        String sql = "DELETE FROM userFriend WHERE (user_id = " + MainMenuActivity.USER_ID + " AND friend_id = '" + id + "') OR (user_id = " + id + " AND friend_id = '" + MainMenuActivity.USER_ID + "')";
        try {
            PreparedStatement s = c.prepareStatement(sql);
            int i = s.executeUpdate();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int updateOnlineUser(int i) {
        Connection c = getC();
        String sql = "UPDATE onlineUser SET status = " + i + " WHERE user_id = '" + MainMenuActivity.USER_ID + "'";
        try {
            PreparedStatement s = c.prepareStatement(sql);
            int i1 = s.executeUpdate();
            c.close();
            return i1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertOnlineUser(String uId) {
        Connection c = getC();
        String sql = "INSERT INTO onlineUser VALUES('" + uId + "', 0)";
        try {
            PreparedStatement s = c.prepareStatement(sql);
            s.executeUpdate();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAllFriendId() {
        Connection c = getC();
        String sql = "SELECT user_id FROM userFriend WHERE friend_id = '" + MainMenuActivity.USER_ID + "'";
        String sql1 = "SELECT friend_id FROM userFriend WHERE user_id = " +MainMenuActivity.USER_ID;
        Set<String> stringSet = new HashSet<>();
        try {
            PreparedStatement s = c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    stringSet.add(set.getString("user_id"));
                }
            }
            s = c.prepareStatement(sql1);
            set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    stringSet.add(set.getString("friend_id"));
                }
            }
            c.close();
            return stringSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringSet;
    }

    public long getBackgroundUpdateTime(String uId) {
        Connection c = getC();
        String sql = "SELECT user_background_image FROM userInfo WHERE user_id = " + uId;
        try {
            PreparedStatement s = c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    String string = set.getString("user_background_image");
                    if (string != null) {
                        long l = Long.parseLong(string);
                        c.close();
                        return l;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getHeadUpdateTime(String uId) {
        Connection c = getC();
        String sql = "SELECT user_background_image FROM userInfo WHERE user_id = " + uId;
        try {
            PreparedStatement s = c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    String string = set.getString("user_background_image");
                    if (string != null) {
                    long l = Long.parseLong(string);
                    c.close();
                    return l;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long updateBackground(String uId) {
        Connection c = getC();
        long l = System.currentTimeMillis();
        String sql = "UPDATE userInfo SET user_image_background = '" + l + "' WHERE user_id = " + uId;
        try {
            PreparedStatement s = c.prepareStatement(sql);
            if (s.executeUpdate() ==0) System.out.println("背景上传失败" + uId);
            c.close();
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long updateHead(String uId) {
        Connection c = getC();
        long l = System.currentTimeMillis();
        String sql = "UPDATE userInfo SET user_image_head = '" + l + "' WHERE user_id = " + uId;
        try {
            PreparedStatement s = c.prepareStatement(sql);
            if (s.executeUpdate() ==0) Log.i("mysql", uId + "头像上传失败");
            c.close();
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int exitMMsg(String mId) {
        Connection c = getC();
        String sql = "DELETE FROM chatList WHERE user_id = '" + MainMenuActivity.USER_ID + "' AND other_id = '" + mId + "' AND is_multiple = 1";
        String sql1 = "DELETE FROM userFriend WHERE user_id = " + MainMenuActivity.USER_ID + " AND friend_id = '" + mId + "' AND is_multiple = 1";
        try {
            PreparedStatement s = c.prepareStatement(sql);
            int a = s.executeUpdate();
            int b = s.executeUpdate(sql1);
            if (a == 1 && b == 1) {
                System.out.println("退出多人删除成功");
                c.close();
                return 1;
            }
            System.out.println("退出多人删除失败");
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return -1;
    }

    public int deleteMMsg(String uId, String oId) {
        Connection c = getC();
        String sql = "DELETE FROM userFriend WHERE user_id = " + uId + ", other_id = '" + oId + "', is_multiple = 1";
        String sql1 = "DELETE FROM chatList WHERE user_id = " + uId + ", other_id = '" + oId + "', is_multiple = 1";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            int a = s.executeUpdate();
            s = (PreparedStatement) c.prepareStatement(sql1);
            int b = s.executeUpdate();
            if (a == 1 && b ==1) {
                c.close();
                return 1;
            } else {
                c.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int inviteMMsg(String id, String msgId) {
        /*
        * 邀请id加入msgId的多人聊天
        *
        * */
        Connection c =getC();
        User u = selectUser(id, 0);
        if (u != null) {
            String sql = "INSERT INTO userFriend (user_id, friend_id, is_multiple)VALUES(" + id + ", '" + msgId + "', 1)";
            try {
                PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
                int a = s.executeUpdate();
                c.close();
                return a;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public String getMMsgName(String id) {
        /*
        * 根据id获取多人聊天名字
        *
        * */
        Connection c = getC();
        String sql = "SELECT msg_name FROM mMsgInfo WHERE m_msg_info_id = " + id;
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    String name = set.getString("msg_name");
                    c.close();
                    return name;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getMMsgName出bug");
        return "";
    }

    public String getMMsgId(String mMsgName, String mMsgOwner) {
        /*
        * 根据名字和创建者id获取多人聊天id
        *
        *
        * */
        Connection c = getC();
        String sql = "SELECT m_msg_info_id FROM mMsgInfo WHERE msg_name = '" + mMsgName + "' AND msg_owner = '" + mMsgOwner + "'";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            while (set.next()) {
                String id = set.getString("m_msg_info_id");
                c.close();
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getMMsgId出现bug");
        return "";
    }

    public boolean hasMMsgName(String mMsgName, String mMsgOwner) {
        /*
        * 检测是否已经存在同名多人
        *
        * */
        Connection c = getC();
        String sql = "SELECT * FROM mMsgInfo WHERE msg_name = '" + mMsgName + "' AND msg_owner = '" + mMsgOwner + "'";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("hasMMsgName出现bug");
        return false;
    }

    public int insertMMsg(String name, String slogan) {
        /*
        * 创建多人聊天，设置拥有者为登录账号
        * 返回值为0插入失败，检查是否重名等别的错误
        * 返回值为1插入成功
        *
        * */
        Connection c = getC();
        int i = 0;
        String sql = "INSERT INTO mMsgInfo (msg_name, msg_slogan, msg_owner)VALUES('" + name + "', '" + slogan + "', '" + MainMenuActivity.USER_ID + "')";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            i = s.executeUpdate();
            if (i == 1) {
                insertChatList(MainMenuActivity.USER_ID, getMMsgId(name, MainMenuActivity.USER_ID), 1);
                addFriend(MainMenuActivity.USER_ID, getMMsgId(name, MainMenuActivity.USER_ID), 1);
            }
            c.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("insertMMsg出现bug");
        return i;
    }


    public boolean hasSChat(String id1, String id2) {
        /*
        * 是否有单人最近聊天
        *
        * */
        Connection c = getC();
        String sql = "SELECT * FROM chatList WHERE (is_multiple = 0 AND user_id = '" + id1 +"' AND other_id ='" + id2 + "') OR (is_multiple = 0 AND user_id = '" + id2 + "' AND other_id = '" + id1 + "')";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                c.close();
                return true;
            } else {
                set.beforeFirst();
                c.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertChatList(String userId, String otherId, int is_multiple) {
        Connection c = getC();
        /*
        * 插入最近聊天表
        * userId 为主id
        * otherId 为对方id
        * is_multiple 为是否为多人群聊
        * */
        String sql = "INSERT INTO chatList (user_id, other_id, is_multiple)VALUES('" + userId + "', '" + otherId +"', " + is_multiple + ")";
        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            if (s.executeUpdate() == 1) {
                System.out.println("添加chatlist成功");
                c.close();
            } else {
                System.out.println("添加chatlist失败");
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChatList(String otherId, int is_multiple) {

        /**
         * 删除最近聊天记录
         * otherId 为被删除id
         * is_multiple 为是否为多人
         */

        Connection c = getC();
        String sql = "DELETE FROM chatList WHERE user_id = '" + MainMenuActivity.USER_ID + "' AND other_id ='" + otherId + "' AND is_multiple = " + is_multiple;
        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            if (s.executeUpdate() == 1) {
                System.out.println("成功删除chatlist");
                c.close();
            } else {
                System.out.println("删除失败chatlist");
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String selectLastMsg(String id1, String id2, int is_multiple) {
        /*
        * 查询最后的信息
        * id1,id2 均可作为判断
        * is_multiple 为是否为多人
        * */
        Connection c1 = getC();
        String sql = null;
        //我感觉这个多人有点问题
        if (is_multiple == 1) {
            sql = "SELECT * FROM mMsg WHERE (send_id = '" + id1 + "' AND receive_id = '" + id2 + "') OR (send_id = '" + id2 + "' AND receive_id = '" + id1 + "')";
        } else {
            sql = "SELECT * FROM sMsg WHERE (send_id = '" + id1 + "' AND receive_id = '" + id2 + "') OR (send_id = '" + id2 + "' AND receive_id = '" + id1 + "')";
        }
//        System.out.println("last" + sql);
        try {
            PreparedStatement s1 = (PreparedStatement) c1.prepareStatement(sql);
            ResultSet set1 = s1.executeQuery();
            set1.last();
            if (set1.getRow() != 0) {
                String msg = set1.getString("msg");
                c1.close();
                return msg;
            } else {
                c1.close();
                return "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<Chat> selectChatList(String id){

        /*
        * 查询用户最近聊天
        * id 为用户id
        * */

        Connection c = getC();
        String sql = "SELECT * FROM chatList WHERE user_id = '" + id + "'";
//        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            List<Chat> list = new LinkedList();
            set.last();
//            System.out.println(set.getRow());
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    Chat chat = new Chat();
                    String other = set.getString("other_id");
                    int isM = set.getInt("is_multiple");
                    String lastMsg = selectLastMsg(MainMenuActivity.USER_ID, other, isM);
                    if (isM == 0) {
                        //单人
                        chat.setOtherId(other);
                        User u = selectUser(other, 0);
                        //记得添加图片功能
                        File f = new File("/data/user/0/com.example.newchat/files/head" + other + ".jpg");
                        Uri uri = Uri.parse(f.getPath());
                        chat.setHeadImage(uri);
                        chat.setName(u.getName());
                        chat.setIsMultiple(isM);
                        chat.setLastMsg(lastMsg);
                    } else {
                        //多人
                        chat.setOtherId(other);
                        chat.setIsMultiple(isM);
                        chat.setName(getMMsgName(other));
                        chat.setLastMsg(lastMsg);
                        File f = new File("/data/user/0/com.example.newchat/files/mhead" + other + ".jpg");
                        Uri uri = Uri.parse(f.getPath());
                        chat.setHeadImage(uri);
                    }
                    list.add(chat);
                }
                c.close();
                return list;
            }
            set.beforeFirst();
            c.close();
            return new LinkedList<>();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public void addsMsg(String userId, String otherId, String msg, int isMultiple) {

        /*
        * 发送消息
        * userId 为发送id
        * otherId 为接收id
        * msg 为消息内容
        * isMultiple 为是否多人
        * */

        Connection c = getC();
        String sql = null;
        if (isMultiple == 1) {
            sql = "INSERT INTO mMsg (send_id, receive_id, msg)VALUES('" + userId + "', '" + otherId + "', '" + msg + "')";
        } else {
            sql = "INSERT INTO sMsg (send_id, receive_id, msg)VALUES('" + userId + "', '" + otherId + "', '" + msg + "')";
        }
        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            System.out.println(s.executeUpdate());
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Msg> selectsMsg(String userId, String otherId, int isMultiple) {

        /*
        * 获取消息内容
        * */

        Connection c =getC();
        String sql = null;
        if (isMultiple == 1) {
            sql = "SELECT * FROM mMsg WHERE receive_id = '" + otherId + "'";
        } else {
            sql = "SELECT * FROM sMsg WHERE (send_id = '" + userId + "' AND receive_id = '" + otherId + "') OR (send_id = '" + otherId + "' AND receive_id = '" + userId + "')";
        }
//        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            List<Msg> list = new LinkedList<>();
                ResultSet set = s.executeQuery();
                set.last();
                if (set.getRow() != 0) {
                    set.beforeFirst();
                    while (set.next()) {
                        Msg msg = new Msg();
                        msg.setMsg(set.getString("msg"));
                        if (set.getString("send_id").equals(MainMenuActivity.USER_ID)) {
                            msg.setMe(true);
                            File file = new File("/data/user/0/com.example.newchat/files/head" + MainMenuActivity.USER_ID + ".jpg");
                            Uri uri = Uri.parse(file.getPath());
                            msg.setHeadImage(uri);
                        } else {
                            msg.setMe(false);
                            File file = new File("/data/user/0/com.example.newchat/files/head" + otherId + ".jpg");
                            Uri uri = Uri.parse(file.getPath());
                            msg.setHeadImage(uri);
                        }
                        list.add(msg);
                    }
                    c.close();
                    return list;
                } else {
                    set.beforeFirst();
                    c.close();
                    return new LinkedList<>();
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }


    public void updateUserInfo(String id, String name, String sex, String slogan, String birthday) {

        /*
        * 更新用户信息
        *
        * */

        Connection c = getC();
        String sql = "UPDATE userInfo set user_name = '" + name + "',user_sex = '" + sex + "',user_slogan = '" + slogan + "', user_birthday = '" + birthday + "' WHERE user_id = " + id;
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            s.executeUpdate();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String email, String password) {
        /*
        * 更新密码
        * */
        Connection c = getC();
        String sql = "UPDATE userInfo SET user_password = '" + password + "' WHERE user_email = '" + email + "'";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            s.executeUpdate();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String returnId(String email) {
        /*
        * 邮箱查找id
        *
        * */
        Connection c = getC();
        String id = null;
        String sql = "SELECT user_id FROM userInfo WHERE user_email = '" + email + "'";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    id = String.valueOf(set.getInt("user_id"));
                }
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void updateFriend(String userId, String otherId) {
        /*
        * 同意好友
        *
        * */
        Connection c = getC();
        String sql = "UPDATE userFriend SET userFriend.is_friend = 1 WHERE (user_id = " + userId + " AND friend_id = '" + otherId + "') OR (user_id = " + otherId + " AND friend_id = '" + userId + "')";
        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            s.executeUpdate();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Friend> selectNewFriendList(String id) {
        /*
         * 获得好友列表
         *
         * */
        Connection c = getC();
        String sql = "SELECT * FROM userFriend WHERE friend_id = '" + id +"'";
        System.out.println(sql);
        List<Friend> list = new LinkedList<>();
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    Friend f = new Friend();
                    int isMultiple = set.getInt("is_multiple");
                    if (isMultiple != 1) {
                        if (set.getString("friend_id").equals(MainMenuActivity.USER_ID)) {
                            f.setfriendId(set.getString("user_id"));
                        } else {
                            f.setfriendId(set.getString("friend_id"));
                        }
                        File file = new File("/data/user/0/com.example.newchat/files/head" + f.getfriendId() + ".jpg");
                        Uri uri = Uri.parse(file.getPath());
                        f.setHeadImage(uri);
                    } else {
                        f.setfriendId(set.getString("friend_id"));
                        File file = new File("/data/user/0/com.example.newchat/files/mhead" + f.getfriendId() + ".jpg");
                        Uri uri = Uri.parse(file.getPath());
                        f.setHeadImage(uri);
                    }

                    //记得修
                    f.setIsMultiple(isMultiple);
                    System.out.println("搜索好友" + f.getfriendId() + "当前时间" + System.currentTimeMillis());
                    f.setName(selectUser(f.getfriendId(), isMultiple).getName());
                    f.setIsFriend(set.getInt("is_friend"));
                    list.add(f);
                }
                c.close();
                return list;
            } else {
                set.beforeFirst();
                c.close();
                return new LinkedList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public List<Friend> selectFriendList(String id) {
        /*
        * 获得好友列表
        *
        * */
        Connection c = getC();
        String sql = "SELECT * FROM userFriend WHERE (user_id = " + id + ") OR (friend_id = '" + id +"')";
        List<Friend> list = new LinkedList<>();
        try {
            PreparedStatement s = c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    Friend f = new Friend();
                    int isMultiple = set.getInt("is_multiple");
                    if (isMultiple != 1) {
                        if (set.getString("friend_id").equals(MainMenuActivity.USER_ID)) {
                            f.setfriendId(set.getString("user_id"));
                        } else {
                            f.setfriendId(set.getString("friend_id"));
                        }
                        String sql1 = "SELECT status FROM onlineUser WHERE user_id = '" + f.getfriendId() + "'";
                        PreparedStatement statement = c.prepareStatement(sql1);
                        ResultSet set1 = statement.executeQuery();
                        set1.next();
                        f.setOnline(set1.getInt("status"));
                        File file = new File("/data/user/0/com.example.newchat/files/head" + f.getfriendId() + ".jpg");
                        Uri uri = Uri.parse(file.getPath());
                        f.setHeadImage(uri);
                    } else {
                        f.setfriendId(set.getString("friend_id"));
                        File file = new File("/data/user/0/com.example.newchat/files/mhead" + f.getfriendId() + ".jpg");
                        Uri uri = Uri.parse(file.getPath());
                        f.setHeadImage(uri);
                    }

                    //记得修
                    f.setIsMultiple(isMultiple);
                    f.setName(selectUser(f.getfriendId(), isMultiple).getName());
                    f.setIsFriend(set.getInt("is_friend"));
                    list.add(f);
                }
                c.close();
                System.out.println(list.get(0).getOnline());
                return list;
            } else {
                set.beforeFirst();
                c.close();
                return new LinkedList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public boolean selectFriend(String userId, String friend_id) {

        /*
        * 是否为好友
        *
        * */

        Connection c = getC();
        String sql = "SELECT * FROM userFriend WHERE (user_id = " + userId + " AND friend_id = '" + friend_id + "') OR (user_id = " + friend_id + " AND friend_id = '" + userId + "')";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                //是好友返回true
                c.close();
                return true;
            } else {
                set.beforeFirst();
                //非好友返回false
                c.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void addFriend(String userId, String otherId, int isMultiple) {
        Connection c = getC();
        //加多人判断
        String sql = "INSERT INTO userFriend (user_id, friend_id, is_friend, is_multiple)VALUES (" + userId + "," + otherId + ", 0, 0)";
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            s.executeUpdate();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insert(String email, String password) {
        /*
        * 注册用户
        *
        * */
        int insertCode = 0;
        Connection c = getC();
        Long time = System.currentTimeMillis();
        String sql = "INSERT INTO userInfo(user_email, user_password, user_image_head, user_background_image) VALUES('" + email + "','" + password +"','" + time + "','" + time + "');";
            try {
                PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
                insertCode = s.executeUpdate();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return insertCode;

    }

    public User selectUser(String id, int isMultiple) {
        Ssh ssh = new Ssh();
        //因为此方法不需要更新头像，所以传入2030年的long值
        ssh.getUserHead(id, 1922328683000l);
        ssh.getUserBackground(id, 1922328683000l);
        /*
        * 查询用户数据
        * */
        String sql = null;
        if (isMultiple == 1) {
            sql = "SELECT * FROM mMsgInfo WHERE m_msg_info_id = " + id;
        } else {
            sql = "SELECT * FROM userInfo WHERE user_id = " + id;
        }
        Connection c = getC();

        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                while (set.next()) {
                    if (isMultiple != 1) {
                        int userId = set.getInt("user_id");
                        String user_name = set.getString("user_name");
                        String user_slogan = set.getString("user_slogan");
                        String user_sex = set.getString("user_sex");
                        String user_birthday = set.getString("user_birthday");
                        User u = new User();
                        u.setId(String.valueOf(userId));
                        u.setName(user_name);
                        u.setSlogan(user_slogan);
                        u.setSex(user_sex);
                        u.setBirthday(user_birthday);
                        c.close();
                        return u;
                    } else {
                        User u = new User();
                        u.setName(set.getString("msg_name"));
                        u.setSlogan(set.getString("msg_slogan"));
                        u.setId(id);
                        u.setHeadImage(R.drawable.icon_test);
                        c.close();
                        return u;
                    }
                }
            } else {
                set.beforeFirst();
                c.close();
                System.out.println("id查询不到");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean selectUser(String id, String password) {
        /*
        * 是否存在用户
        *
        * */
        String sql = "SELECT * FROM userInfo WHERE user_id = " + id;
        Connection c = getC();
        try {
                PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
                ResultSet set = s.executeQuery();
                String user_password = null;

                while (set.next()) {
                    user_password = set.getString("user_password");
                }
            if (user_password != null) {
                if (user_password.equals(password)) {
                    System.out.println("密码正确");

                    c.close();
                    return true;
                }else {
                    System.out.println("密码错误");

                    c.close();
                    return false;
                }
            }else {
                System.out.println("查询不到相关信息");

                c.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            }
        System.out.println("查询完毕");
            return false;
    }

    public boolean selectUserId(String id) {
        /*
        * 是否存在用户
        *
        * */
        Connection c = getC();
        String sql = "SELECT user_id FROM userInfo WHERE user_id = " + id;
        System.out.println(sql);
        try {
            PreparedStatement s = (PreparedStatement) c.prepareStatement(sql);
            ResultSet set = s.executeQuery();
            set.last();
            if (set.getRow() != 0) {
                set.beforeFirst();
                c.close();
                System.out.println("存在用户");
                return true;
            }else {
                set.beforeFirst();
                c.close();
                System.out.println("不存在用户");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("数据库查询出错 位置selectUserId");
        return false;
    }


    private static Connection getC() {
        /*
        * 连接数据库
        *
        * */
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:25562/android?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true", "android", "123456");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

}
