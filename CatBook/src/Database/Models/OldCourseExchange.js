import { Model } from '@nozbe/watermelondb';
import { date, readonly, text, number } from '@nozbe/watermelondb/decorators';

export default class CourseExchange extends Model {
    static table = 'courses_exchange';

    @readonly @date('created_at') createdAt;
   //@number('type') type;
    @text('mid') mid;
    @date('effectived_at') effectivedAt;

}