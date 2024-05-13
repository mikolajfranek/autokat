import Realm, { BSON, ObjectSchema } from "realm";

// Define your object model
export class Filter extends Realm.Object<Filter> {
    _id!: BSON.ObjectId;
    name!: string;

    static schema: ObjectSchema = {
        name: 'Filter',
        properties: {
            _id: 'objectId',
            name: { type: 'string', indexed: 'full-text' },
        },
        primaryKey: '_id',
    };
}