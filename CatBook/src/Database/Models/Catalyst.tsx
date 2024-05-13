import Realm, { BSON, ObjectSchema } from "realm";

// Define your object model
export class Catalyst extends Realm.Object<Catalyst> {
    _id!: BSON.ObjectId;
    name!: string;

    static schema: ObjectSchema = {
        name: 'Catalyst',
        properties: {
            _id: 'objectId',
            name: { type: 'string', indexed: 'full-text' },
        },
        primaryKey: '_id',
    };
}