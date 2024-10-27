package org.example;

public class Storage extends Item implements Signable{
    protected Boolean getssignal;
    public Storage(String storageName, String storageType,Boolean stackable,Boolean getssignal) {
        super(storageName, storageType, stackable);
        this.getssignal = getssignal;
    }
    @Override
    public Boolean getsSignaled(){
        return getssignal;
    }
}
