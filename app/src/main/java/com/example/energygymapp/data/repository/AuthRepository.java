package com.example.energygymapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Класс репозитория, ответственного за локальную систему управления пользователями
 */
public class AuthRepository {

    public static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public static String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static void signOut() {
        firebaseAuth.signOut();
    }
}
