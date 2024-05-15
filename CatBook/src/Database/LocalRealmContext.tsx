import CourseExchange from './Models/CourseExchange';
import CourseMetal from './Models/CourseMetal';
import Catalyst from './Models/Catalyst';
import Filter from './Models/Filter';
import { createRealmContext } from '@realm/react';
import Realm from "realm";

export const LocalRealmContext = createRealmContext({
    schema: [CourseExchange, CourseMetal, Catalyst, Filter],

    /**
     * If your schema update adds optional properties or removes properties, 
     * Realm can perform the migration automatically. 
     * You only need to increment the schemaVersion.
     */
    schemaVersion: 0,

    /**
     * For more complex schema updates, you must also manually specify 
     * the migration logic in a migration function. 
     */
    onMigration: (oldRealm: Realm, newRealm: Realm) => {
        // // example of rename property
        // if (oldRealm.schemaVersion < 1) {
        //     const oldObjects: Realm.Results<OldObjectModel> =
        //         oldRealm.objects(OldObjectModel);
        //     const newObjects: Realm.Results<Person> = newRealm.objects(Person);
        //     // loop through all objects and set the fullName property in the
        //     // new schema
        //     for (const objectIndex in oldObjects) {
        //         const oldObject = oldObjects[objectIndex];
        //         const newObject = newObjects[objectIndex];
        //         newObject.fullName = `${oldObject.firstName} ${oldObject.lastName}`;
        //     }
        // }

        // // example of modify a property type
        // if (oldRealm.schemaVersion < 1) {
        //     const oldObjects: Realm.Results<OldObjectModel> =
        //         oldRealm.objects(Person);
        //     const newObjects: Realm.Results<Person> = newRealm.objects(Person);
        //     // Loop through all objects and set the _id property
        //     // in the new schema.
        //     for (const objectIndex in oldObjects) {
        //         const oldObject = oldObjects[objectIndex];
        //         const newObject = newObjects[objectIndex];
        //         newObject._id = new Realm.BSON.ObjectId(oldObject._id);
        //     }
        // }
    }
});