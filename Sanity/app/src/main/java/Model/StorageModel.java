package Model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Yifan on 10/13 013.
 */

public class StorageModel {
    private static StorageModel mInstance = null;
    private static Context mContext;
    private FileOutputStream fos;
    private FileInputStream fis;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     * Constructor (which is empty)
     */
    public StorageModel() {
    }


    //<editor-fold desc="Singleton & Init">
    /* =============== Singleton & Init =============== */

    /**
     * Get the instance of this model
     *
     * @return <b>StorageModel</b> a StorageModel instance
     */
    public static StorageModel GetInstance() {
        if (mInstance == null) {
            mInstance = new StorageModel();
        }
        return mInstance;
    }

    /**
     * Set Application Context (Should be called at the very early stage like onStart)
     *
     * @param context a <b>Context</b>
     */
    public static void SetContext(Context context) {
        mContext = context;
    }
    //</editor-fold>


    //<editor-fold desc="File Operation">
    /* =============== File Operation =============== */

    /**
     * Test if all 3 model files exist in internal storage
     *
     * @return <b>boolean</b> Yes or No
     */
    public boolean AreFilesExist() {
        File file = mContext.getFileStreamPath("SanityVariable.dat");
        if (!file.exists())
            return false;
        file = mContext.getFileStreamPath("SanityBudgetModel.dat");
        if (!file.exists())
            return false;
        file = mContext.getFileStreamPath("SanityCategoryModel.dat");
        if (!file.exists())
            return false;
        file = mContext.getFileStreamPath("SanityTransactionModel.dat");
        if (!file.exists())
            return false;
        return true;
    }

    /**
     * Delete all 3 model files from internal storage
     *
     * @return <b>boolean</b> Success or not
     */
    public void DeleteFiles() {
        File file = mContext.getFileStreamPath("SanityVariable.dat");
        try {
            file.delete();
        } catch (Exception e) {
            System.err.println("StorageModel: Couldn't delete SanityVariable.dat");
        }
        file = mContext.getFileStreamPath("SanityBudgetModel.dat");
        try {
            file.delete();
        } catch (Exception e) {
            System.err.println("StorageModel: Couldn't delete SanityBudgetModel.dat");
        }
        file = mContext.getFileStreamPath("SanityCategoryModel.dat");
        try {
            file.delete();
        } catch (Exception e) {
            System.err.println("StorageModel: Couldn't delete SanityCategoryModel.dat");
        }
        file = mContext.getFileStreamPath("SanityTransactionModel.dat");
        try {
            file.delete();
        } catch (Exception e) {
            System.err.println("StorageModel: Couldn't delete SanityTransactionModel.dat");
        }
    }
    //</editor-fold>


    //<editor-fold desc="All Model R&W">
    /* =============== All Model R&W =============== */

    /**
     * Save all 3 models into internal storage
     *
     * @return <b>boolean</b> Success or not
     */
    public boolean SaveAll() {
        if (!SaveObject(Variable.GetInstance()))
            return false;
        if (!SaveObject(BudgetModel.GetInstance()))
            return false;
        if (!SaveObject(CategoryModel.GetInstance()))
            return false;
        if (!SaveObject(TransactionModel.GetInstance()))
            return false;
        return true;
    }

    /**
     * Read all 3 models from internal storage
     */
    public void ReadAll() {
        Variable.UpdateInstance((Variable) ReadObject("Variable"));
        BudgetModel.UpdateInstance((BudgetModel) ReadObject("BudgetModel"));
        CategoryModel.UpdateInstance((CategoryModel) ReadObject("CategoryModel"));
        TransactionModel.UpdateInstance((TransactionModel) ReadObject("TransactionModel"));
    }
    //</editor-fold>


    //<editor-fold desc="One Model R&W">
    /* =============== One Model R&W =============== */

    /**
     * save one model into internal storage
     *
     * @param model one of 3 models
     * @param <E>   <b>Model</b> class
     * @return <b>boolean</b> Success or not
     */
    public <E> boolean SaveObject(E model) {
        try {
            fos = mContext.openFileOutput("Sanity" + model.getClass().getSimpleName() + ".dat", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(model);
            oos.close();
            fos.close();
            Log.d("DEBUG", model.getClass().getSimpleName() + " Object Saved");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Read one model into internal storage
     *
     * @param className <b>String</b> class name of a model
     * @param <E>       <b>Model</b> class
     * @return <b>E</b> a model class
     */
    public <E> E ReadObject(String className) {
        try {
            E currModel;
            fis = mContext.openFileInput("Sanity" + className + ".dat");
            ois = new ObjectInputStream(fis);
            currModel = (E) ois.readObject();
            ois.close();
            fis.close();
            Log.d("DEBUG", className + " Object Read");
            return currModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>
}
