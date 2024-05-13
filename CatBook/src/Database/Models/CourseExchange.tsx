import Realm, { BSON, ObjectSchema } from "realm";

//https://www.mongodb.com/docs/atlas/device-sdks/sdk/react-native/quick-start/
export class CourseExchange extends Realm.Object<CourseExchange> {
    _id!: BSON.ObjectId;
    name!: string;
    effectived_at?: string;

    static schema: ObjectSchema = {
        name: 'CourseExchange',
        properties: {
            _id: 'objectId',
            name: { type: 'string', indexed: 'full-text' },
            effectived_at: { type: 'string' }
        },
        primaryKey: '_id',
    };
}