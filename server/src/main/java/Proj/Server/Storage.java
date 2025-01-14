package Proj.Server;

public class Storage extends Item implements Signable{
    protected Boolean getssignal;

    public Storage(String storageName, String storageType, Boolean stackable, Boolean getssignal, Stacksize stackSize) {
        super(storageName, storageType, stackable, stackSize);
        this.getssignal = getssignal;
    }
    @Override
    public Boolean getsSignaled(){
        return getssignal;
    }
}
