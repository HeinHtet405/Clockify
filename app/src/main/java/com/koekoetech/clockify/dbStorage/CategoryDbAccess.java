package com.koekoetech.clockify.dbStorage;

import com.koekoetech.clockify.models.Category;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryDbAccess {

    private Realm mRealm;

    public CategoryDbAccess(Realm realm) {
        this.mRealm = realm;
    }

    public void insertUpdateCategory(Category category) {
        mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(category));
    }

    public List<Category> getAllCategoryList() {
        RealmResults<Category> categoryRealmResults = mRealm.where(Category.class).findAll();
        List<Category> projectList = new ArrayList<>();
        for (Category project : categoryRealmResults) {
            projectList.add(mRealm.copyFromRealm(project));
        }
        return projectList;
    }

    public Category getFirstCategory() {
        return mRealm.where(Category.class).findFirst();
    }

    public Category getCategory(String categoryId) {
        return mRealm.where(Category.class).equalTo(Category.FIELD_CATEGORY_ID, categoryId).findFirst();
    }

    public void deleteCategory(String categoryId) {
        Category category = mRealm.where(Category.class).equalTo(Category.FIELD_CATEGORY_ID, categoryId).findFirst();
        if (category != null) {
            mRealm.executeTransaction(realm -> category.deleteFromRealm());
        }
    }
}
