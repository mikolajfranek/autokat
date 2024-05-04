import { Model } from '@nozbe/watermelondb';
import { date, readonly, text, writer } from '@nozbe/watermelondb/decorators';

export default class CourseMetal extends Model {
    static table = 'courses_metal';

    @readonly @date('created_at') createdAt;
    @text('platinum') platinum;
    @text('palladium') palladium;
    @text('rhodium') rhodium;

    @writer async add(platinumInput, palladiumInput, rhodiumInput) {
        const newItem = await this.collections.get(this.table).create(item => {
            item.platinum = platinumInput;
            item.palladium = palladiumInput;
            item.rhodium = rhodiumInput;
        });
        return newItem
    }
}