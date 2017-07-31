package com.infinity.lunadd.mvp.model.rx;

import android.content.res.Resources;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.exceptions.HyphenateException;
import com.infinity.lunadd.R;
import com.infinity.lunadd.mvp.model.bean.Comment;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.model.dao.CommentDao;
import com.infinity.lunadd.mvp.model.dao.NewsDao;
import com.infinity.lunadd.mvp.model.dao.PostDao;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DanielChu on 2017/6/7.
 * 用Rxjava封装了LeanCloud的Sdk
 */
public class RxLeanCloud {
    private static final String TAG = RxLeanCloud.class.getSimpleName();


    public Observable<Post> SavePostByLeanCloud(final Post post) {
        return Observable.create(new Observable.OnSubscribe<Post>() {
            @Override
            public void call(final Subscriber<? super Post> subscriber) {
                Logger.d("SavePostByLeanCloud");
                post.setFetchWhenSave(true);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Logger.d("SavePostByLeanCloud Done");
                        if (e == null) {

                            subscriber.onNext(post);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AVUser> SaveUserByLeanCloud(final AVUser user) {
        return Observable.create(new Observable.OnSubscribe<AVUser>() {
            @Override
            public void call(final Subscriber<? super AVUser> subscriber) {

                user.setFetchWhenSave(true);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(user);
                        } else {
                            subscriber.onError(e);
                            Logger.e(e.getMessage());
                        }
                        subscriber.onCompleted();
                    }
                });

            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<AVUser> GetUserByUserStatus(final boolean waiting, final List<String> preferLanguage, final int duration) {
        return Observable.create(new Observable.OnSubscribe<AVUser>() {
            @Override
            public void call(Subscriber<? super AVUser> subscriber) {
                AVQuery<AVUser> query_waiting = AVUser.getQuery();
                AVQuery<AVUser> query_preferlang = AVUser.getQuery();
                AVQuery<AVUser> query_duration = AVUser.getQuery();
                try {
                    query_waiting.whereEqualTo(UserDao.WAITING, waiting);
                    query_duration.whereEqualTo(UserDao.DURATION, duration);
                    Logger.d(preferLanguage.get(0));
                    query_preferlang.whereContains(UserDao.PREFERLANGUAGE,preferLanguage.get(0));
                    for (int i = 1; i < preferLanguage.size(); i++){
                        AVQuery<AVUser> query_temp = AVUser.getQuery();
                        Logger.d(preferLanguage.get(i));
                        query_temp.whereContains(UserDao.PREFERLANGUAGE,preferLanguage.get(i));
                        query_preferlang = AVQuery.or(Arrays.asList(query_preferlang, query_temp));
                    }
                    AVQuery<AVUser> query = AVQuery.and(Arrays.asList(query_waiting, query_preferlang,query_duration));
                    AVUser user = query.getFirst();
                    subscriber.onNext(user);
                } catch (AVException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io());

    }

    public Observable<List<AVUser>> GetUserListByUserStatus(final boolean waiting, final List<String> preferLanguage, final int duration, final Date date) {
        return Observable.create(new Observable.OnSubscribe<List<AVUser>>() {
            @Override
            public void call(final Subscriber<? super List<AVUser>> subscriber) {
                AVQuery<AVUser> query_waiting = AVUser.getQuery();
                AVQuery<AVUser> query_preferlang = AVUser.getQuery();
                AVQuery<AVUser> query_duration = AVUser.getQuery();
                AVQuery<AVUser> query_date = AVUser.getQuery();
                query_waiting.whereEqualTo(UserDao.WAITING, waiting);
                query_duration.whereEqualTo(UserDao.DURATION, duration);
                query_date.whereGreaterThan(UserDao.UPDATEDAT,date);
                Logger.d(preferLanguage.get(0));
                query_preferlang.whereContains(UserDao.PREFERLANGUAGE,preferLanguage.get(0));
                for (int i = 1; i < preferLanguage.size(); i++){
                    AVQuery<AVUser> query_temp = AVUser.getQuery();
                    Logger.d(preferLanguage.get(i));
                    query_temp.whereContains(UserDao.PREFERLANGUAGE,preferLanguage.get(i));
                    query_preferlang = AVQuery.or(Arrays.asList(query_preferlang, query_temp));
                }
                AVQuery<AVUser> query = AVQuery.and(Arrays.asList(query_waiting, query_preferlang,query_duration,query_date));
                Logger.d(Thread.currentThread().getName());
                query.findInBackground(new FindCallback<AVUser>() {
                    @Override
                    public void done(List<AVUser> list, AVException e) {
                        if (e == null) {
                            subscriber.onNext(list);
                        } else {
                            subscriber.onError(e);
                            Logger.e(e.getMessage());
                        }
                        subscriber.onCompleted();

                    }
                });


            }
        }).subscribeOn(Schedulers.io());

    }

    public Observable<List<Post>> GetALlPostByLeanCloud(final List<String> preferLanguage, final int size, final int page) {
        return Observable.create(new Observable.OnSubscribe<List<Post>>() {
            @Override
            public void call(final Subscriber<? super List<Post>> subscriber) {

                List<AVQuery<Post>> queries = new ArrayList<>();
                for (int i = 0; i < preferLanguage.size(); i++){
                    AVQuery<Post> query_temp = AVQuery.getQuery(PostDao.TABLE_NAME);
                    Logger.d(preferLanguage.get(i));
                    query_temp.whereContains(PostDao.POST_LANGUAGE,preferLanguage.get(i));
                    queries.add(query_temp);
                }
                AVQuery<Post> mainquery = AVQuery.or(queries);
                mainquery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                mainquery.setLimit(size);
                mainquery.setSkip(size * page);
                mainquery.include(PostDao.POST_AUTHOR);
                mainquery.orderByDescending(PostDao.POST_UPDATEAT);
                mainquery.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> list, AVException e) {
                        if (e == null) {
                            subscriber.onNext(list);
                        } else {
                            Logger.e(e.getMessage());
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();

                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }


    public Observable<String> UploadPicture(final AVFile avFile) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Logger.d("UploadPicture");
                avFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(avFile.getUrl());
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }
/*
    public Observable<List<Gallery>> FetchAllPicture(final String authorId, final String theotherone, final boolean isFirst, final int size, final int page) {
        return Observable.create(new Observable.OnSubscribe<List<Gallery>>() {
            @Override
            public void call(final Subscriber<? super List<Gallery>> subscriber) {
                AVQuery<Gallery> query = AVQuery.getQuery(Gallery.class);
                query.whereEqualTo(GalleryDao.AUTHOR_ID, authorId);
                AVQuery<Gallery> query1 = AVQuery.getQuery(Gallery.class);
                query1.whereEqualTo(GalleryDao.AUTHOR_ID, theotherone);
                List<AVQuery<Gallery>> queries = new ArrayList<>();
                queries.add(query);
                queries.add(query1);
                AVQuery<Gallery> mainquery = AVQuery.or(queries);
                mainquery.orderByDescending("createdAt");
                mainquery.include(GalleryDao.AUTHOR);
                if (isFirst) {
                    mainquery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                } else {
                    mainquery.setLimit(size);
                    mainquery.skip(size * page);
                    mainquery.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
                }
                mainquery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                mainquery.findInBackground(new FindCallback<Gallery>() {

                    @Override
                    public void done(List<Gallery> list, AVException e) {
                        if (e == null) {
                            subscriber.onNext(list);

                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();

                    }
                });

            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Gallery> saveGallery(final Gallery gallery) {
        return Observable.create(new Observable.OnSubscribe<Gallery>() {
            @Override
            public void call(final Subscriber<? super Gallery> subscriber) {
                gallery.setFetchWhenSave(true);
                gallery.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(gallery);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });


            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
*/
    public Observable<Boolean> PushToOtherUser(final AVUser user, final String content, final int action) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {

                AVPush push = new AVPush();
                String installationId = user.getString(UserDao.OTHERUSERINSTALLATIONID);

                AVQuery<AVInstallation> query = AVInstallation.getQuery();
                query.whereEqualTo("installationId", installationId);
                push.setQuery(query);

                Logger.e(installationId);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(NewsDao.ACTION, "com.infinity.lunadd.Push");
                jsonObject.put(NewsDao.CONTENT, content);
                jsonObject.put(NewsDao.INSTALLATIONI_ID,
                        installationId);
                jsonObject.put(NewsDao.TITLE, action == 0 ? R.string.confirm : R.string.timeup);
                jsonObject.put(NewsDao.TIME, System.currentTimeMillis());
                jsonObject.put(NewsDao.USERID,user.getUsername());
                jsonObject.put(NewsDao.USERNAME,user.get(UserDao.NICK));
                jsonObject.put(NewsDao.USERINSTALLATIONID,user.get(UserDao.INSTALLATIONID));
                push.setData(jsonObject);


//                Map<String, Object> map = new HashMap<>();
//                map.put(NewsDao.CONTENT, content);
//                map.put(NewsDao.ACTION, "com.infinity.lunadd.Push");
//                map.put(NewsDao.INSTALLATIONI_ID,
//                        installationId);
//                map.put(NewsDao.TITLE, action == 0 ? R.string.confirm : R.string.timeup);
//                map.put(NewsDao.TIME, System.currentTimeMillis());
//                map.put(NewsDao.USER,user);
//                push.setData(map);


             //   push.setMessage(content);
                push.setPushToAndroid(true);

                /*
                AVPush push = new AVPush();

                AVQuery<AVInstallation> query = AVInstallation.getQuery();
                query.whereEqualTo("installationId", AVInstallation.getCurrentInstallation()
                        .getInstallationId());
                push.setQuery(query);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", "com.infinity.lunadd.Push");
                jsonObject.put("alert", "Test");

                push.setData(jsonObject);
                push.setPushToAndroid(true);
                */
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(true);
                        } else {
                            Logger.e("PushToOtherUser:"+e.getMessage());
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();

                    }
                });

            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


    public Observable<String> SaveInstallationId() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(AVInstallation.getCurrentInstallation().getInstallationId());
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public Observable<AVUser> GetUserByUserid(final String userid) {
        return Observable.create(new Observable.OnSubscribe<AVUser>() {
            @Override
            public void call(Subscriber<? super AVUser> subscriber) {
                AVQuery<AVUser> query = AVUser.getQuery();
                try {
                    AVUser user = query.whereEqualTo(UserDao.USERID, userid).getFirst();
                    subscriber.onNext(user);
                } catch (AVException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io());

    }


    public Observable<AVUser> Login(final String userid, final String passwd) {
        return Observable.create(new Observable.OnSubscribe<AVUser>() {
            @Override
            public void call(final Subscriber<? super AVUser> subscriber) {

                AVUser.logInInBackground(userid, passwd, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser user, AVException e) {
                        if (e == null) {
                            subscriber.onNext(user);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }

                }, AVUser.class);

            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> Register(final String userid, final String passwd, final String username, final List<String> preferlanguage) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(final Subscriber<? super User> subscriber) {

                final User user = new User();
                user.setUsername(userid);
                user.setPassword(passwd);
                user.setNick(username);
                user.setPreferLanguage(preferlanguage);
                user.initialize();
                user.setFetchWhenSave(true);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(user);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });

    }

    public Observable<Boolean> HXRegister(final String userid, final String passwd) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    EMClient.getInstance().createAccount(userid, passwd);

                    subscriber.onNext(true);

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> HXLogin(final String userid, final String passwd) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                EMClient.getInstance().login(userid, passwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(int i, String s) {
                        subscriber.onError(new Throwable(s));
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });

    }

    public Observable<List<Comment>> getComments(final Post post, final int page, final int size) {

        return Observable.create(new Observable.OnSubscribe<List<Comment>>() {
            @Override
            public void call(final Subscriber<? super List<Comment>> subscriber) {
                AVQuery<Comment> query = AVObject.getQuery(Comment.class);
                query.whereEqualTo(CommentDao.POST, post);
                query.setLimit(size);
                query.setSkip(page * size);
                query.orderByDescending("createdAt");
                query.include(CommentDao.REPLY_TO);
                query.include(CommentDao.AUTHOR);
                query.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> list, AVException e) {
                        if (e == null) {
                            subscriber.onNext(list);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public Observable<Comment> createComment(final Comment comment) {

        return Observable.create(new Observable.OnSubscribe<Comment>() {
            @Override
            public void call(final Subscriber<? super Comment> subscriber) {
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(comment);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });

            }
        });
    }


    public Observable<Integer> incrementComments(final Post post) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                post.setFetchWhenSave(true);
                post.increment(PostDao.POST_COMMENT_COUNT);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(post.getCommentcount());
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });

            }
        });

    }

    public Observable<Boolean> delete(final AVObject obj) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                obj.deleteEventually(new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            subscriber.onNext(true);
                        } else {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


}
