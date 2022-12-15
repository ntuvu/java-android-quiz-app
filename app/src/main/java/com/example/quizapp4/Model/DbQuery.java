package com.example.quizapp4.Model;

import com.example.quizapp4.MyCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;
    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catlist = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testlist = new ArrayList<>();
    public static ProfileModel myProfile = new ProfileModel("NA", null, null, 0);
    public static int g_selected_test_index = 0;
    public static List<QuestionModel> g_quesList = new ArrayList<>();
    public static RankModel myPerformance = new RankModel("NULL", 0, -1);
    public static List<RankModel> g_usersList = new ArrayList<>();
    public static int g_usersCount = 0;
    public static boolean isMeOnTopList = false;
    public static List<String> g_bmIdList = new ArrayList<>();

    public static void getUserData(MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    myProfile.setName(documentSnapshot.getString("NAME"));
                    myProfile.setEmail(documentSnapshot.getString("EMAIL_ID"));

                    if (documentSnapshot.getString("PHONE") != null) {
                        myProfile.setPhone(documentSnapshot.getString("PHONE"));
                    }
                    if (documentSnapshot.get("BOOKMARKS") != null) {
                        myProfile.setBookmarksCount(documentSnapshot.getLong("BOOKMARKS").intValue());
                    }

                    myPerformance.setScore(documentSnapshot.getLong("TOTAL_SCORE").intValue());
                    myPerformance.setName(documentSnapshot.getString("NAME"));

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void createUserData(String email, String name, MyCompleteListener completeListener) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("TOTAL_SCORE", 0);
        userData.put("BOOKMARKS", 0);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        WriteBatch batch = g_firestore.batch();
        batch.set(userDoc, userData);
        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc, "COUNT", FieldValue.increment(1));
        batch.commit()
                .addOnSuccessListener(unused -> {
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void loadCategories(MyCompleteListener completeListener) {
        g_catlist.clear();
        g_firestore.collection("QUIZ").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, QueryDocumentSnapshot> docList = new HashMap<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        docList.put(doc.getId(), doc);
                    }

                    QueryDocumentSnapshot catListDoc = docList.get("Categories");

                    long catCount = catListDoc.getLong("COUNT");

                    for (int i = 1; i <= catCount; i++) {
                        String catID = catListDoc.getString("CAT" + i + "_ID");
                        QueryDocumentSnapshot catDoc = docList.get(catID);
                        int noOfTest = catDoc.getLong("NO_OF_TESTS").intValue();
                        String catName = catDoc.getString("NAME");
                        g_catlist.add(new CategoryModel(catID, catName, noOfTest));
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {

                });
    }

    public static void loadTestData(MyCompleteListener completeListener) {
        g_testlist.clear();
        g_firestore.collection("QUIZ").document(g_catlist.get(g_selected_cat_index).getDocID())
                .collection("TESTS_LIST").document("TESTS_INFO")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int noOfTests = g_catlist.get(g_selected_cat_index).getNoOfTests();

                    for (int i = 1; i <= noOfTests; i++) {
                        g_testlist.add(new TestModel(
                                documentSnapshot.getString("TEST" + i + "_ID"), 0, documentSnapshot.getLong("TEST" + i + "_TIME").intValue()
                        ));
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }


    public static void loadData(MyCompleteListener completeListener) {
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        getUsersCount(new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                loadBmIds(completeListener);
                            }

                            @Override
                            public void onFailure() {
                                completeListener.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        completeListener.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                completeListener.onFailure();
            }
        });
    }

    public static void loadQuestion(MyCompleteListener completeListener) {
        g_quesList.clear();
        g_firestore.collection("Questions")
                .whereEqualTo("CATEGORY", g_catlist.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testlist.get(g_selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        boolean isBookmarked = g_bmIdList.contains(doc.getId());

                        g_quesList.add(new QuestionModel(
                                doc.getId(),
                                doc.getString("QUESTION"),
                                doc.getString("A"),
                                doc.getString("B"),
                                doc.getString("C"),
                                doc.getString("D"),
                                doc.getLong("ANSWER").intValue(),
                                -1,
                                NOT_VISITED,
                                isBookmarked
                        ));
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void saveResult(int score, MyCompleteListener completeListener) {
        WriteBatch batch = g_firestore.batch();

        Map<String, Object> bmData = new HashMap<>();
        for (int i = 0; i < g_bmIdList.size(); i++) {
            bmData.put("BM" + (i + 1) + "_ID", g_bmIdList.get(i));
        }

        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");

        batch.set(bmDoc, bmData);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());

        Map<String, Object> userData = new HashMap<>();
        userData.put("TOTAL_SCORE", score);
        userData.put("BOOKMARKS", g_bmIdList.size());

        batch.update(userDoc, userData);

        if (score > g_testlist.get(g_selected_test_index).getTopScore()) {
            DocumentReference scoreDoc = userDoc.collection("USER_DATA").document("MY_SCORES");

            Map<String, Object> testData = new HashMap<>();
            testData.put(g_testlist.get(g_selected_test_index).getTestID(), score);
            batch.set(scoreDoc, testData, SetOptions.merge());
        }

        batch.commit()
                .addOnSuccessListener(unused -> {
                    if (score > g_testlist.get(g_selected_test_index).getTopScore()) {
                        g_testlist.get(g_selected_test_index).setTopScore(score);
                    }

                    myPerformance.setScore(score);
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {

                });
    }

    public static void loadMyScore(MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    for (int i = 0; i < g_testlist.size(); i++) {
                        int top = 0;
                        if (documentSnapshot.get(g_testlist.get(i).getTestID()) != null) {
                            top = documentSnapshot.getLong(g_testlist.get(i).getTestID()).intValue();
                        }

                        g_testlist.get(i).setTopScore(top);
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void saveProfileData(String name, String phone, MyCompleteListener completeListener) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("NAME", name);

        if (phone != null) {
            profileData.put("PHONE", phone);
        }

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .update(profileData)
                .addOnSuccessListener(unused -> {
                    myProfile.setName(name);

                    if (phone != null) {
                        myProfile.setPhone(phone);
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void getTopUsers(MyCompleteListener completeListener) {
        g_usersList.clear();
        String myUID = FirebaseAuth.getInstance().getUid();

        g_firestore.collection("USERS")
                .whereGreaterThan("TOTAL_SCORE", 0)
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int rank = 1;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        g_usersList.add(new RankModel(
                                doc.getString("NAME"),
                                doc.getLong("TOTAL_SCORE").intValue(),
                                rank
                        ));

                        if (myUID.compareTo(doc.getId()) == 0) {
                            isMeOnTopList = true;
                            myPerformance.setRank(rank);
                        }

                        rank++;
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void getUsersCount(MyCompleteListener completeListener) {
        g_firestore.collection("USERS")
                .document("TOTAL_USERS")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    g_usersCount = documentSnapshot.getLong("COUNT").intValue();
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }

    public static void loadBmIds(MyCompleteListener completeListener) {
        g_bmIdList.clear();
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int count = myProfile.getBookmarksCount();

                    for (int i = 0; i < count; i++) {
                        String bmID = documentSnapshot.getString("BM" + (i + 1) + "_ID");
                        g_bmIdList.add(bmID);
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    completeListener.onFailure();
                });
    }
}
