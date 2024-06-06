package server.utility;


import common.data.SpaceMarine;
import server.App;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Collection manager.
 * Collection used is LinkedList
 * Elements are of class Space marine
 */
public class CollectionManager {
    public List<SpaceMarine> list = new LinkedList<>();
    public List<SpaceMarine> spaceMarineCollection;
    private final ZonedDateTime creationDate;


    /**
     * Collection manager constructor.
     */
    public CollectionManager() {
        spaceMarineCollection =Collections.synchronizedList(list);
        creationDate = ZonedDateTime.now();
    }

    /**
     * Creation date getter.
     * @return Creation date of collection manager.
     */
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Get collection.
     * @return Linked list of space marines.
     */
    public List<SpaceMarine> getSpaceMarineCollection() {
        return spaceMarineCollection;
    }

    /**
     * Get size of a collection.
     * @return Collection size.
     */
    public int getSize() {
        return spaceMarineCollection.size();
    }

    public void updateCollection(LinkedList<SpaceMarine> collection){
        this.spaceMarineCollection = collection;
        App.logger.info("Collection updated successfully!");
    }

    /**
     * Remove element from collection by index.
     * @param index Index of a space marine that needs to be removed.
     */
    public void removeAtIndex(int index) {
        spaceMarineCollection.remove(index);
    }
}

