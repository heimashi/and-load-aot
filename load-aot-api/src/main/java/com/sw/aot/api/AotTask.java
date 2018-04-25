package com.sw.aot.api;

public class AotTask {

    private int id;

    String router;

    AotTask(int id, String router) {
        this.id = id;
        this.router = router;
    }

    String getTaskKey() {
        return router + "_" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AotTask aotTask = (AotTask) o;

        if (id != aotTask.id) {
            return false;
        }
        return router != null ? router.equals(aotTask.router) : aotTask.router == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (router != null ? router.hashCode() : 0);
        return result;
    }
}
