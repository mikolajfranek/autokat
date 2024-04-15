import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';

export default class CourseMetal extends Model {
    static table = 'courses_metal';

    @readonly @date('created_at') createdAt;
    @text('platinum') platinum;
    @text('palladium') palladium;
    @text('rhodium') rhodium;
}