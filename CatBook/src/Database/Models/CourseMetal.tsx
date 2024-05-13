import Realm, { BSON, ObjectSchema } from "realm";

// Define your object model
export class CourseMetal extends Realm.Object<CourseMetal> {
    _id!: BSON.ObjectId;
    name!: string;

    static schema: ObjectSchema = {
        name: 'CourseMetal',
        properties: {
            _id: 'objectId',
            name: { type: 'string', indexed: 'full-text' },
        },
        primaryKey: '_id',
    };
}