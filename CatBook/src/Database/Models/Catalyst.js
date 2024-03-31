import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';
import { writer } from '@nozbe/watermelondb/decorators'

export default class Course extends Model {
    static table = 'catalysts';

    @readonly @date('created_at') createdAt;
    @text('picture_id') pictureId;
    @text('thumbnail') thumbnail;
    @text('is_thumbnail') isThumbnail;
    @text('name') name;
    @text('brand') brand;
    @text('platinum') platinum;
    @text('palladium') palladium;
    @text('rhodium') rhodium;
    @text('type') type;
    @text('weight') weight;

    @writer async setThumbnail(inputThumbnail) {
        await this.update(catalyst => {
            catalyst.thumbnail = inputThumbnail;
            catalyst.isThumbnail = true;
        });
    }
}