package imis.client;

import imis.client.customviews.BlockView;
import imis.client.customviews.BlocksLayout;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.blocks_content);

    final BlocksLayout blocks = (BlocksLayout) findViewById(R.id.blocks);
    long start = System.currentTimeMillis() - 2 * 60 * 60 * 1000;
    // long end = System.currentTimeMillis() - 1 * 59 * 60 * 1000 - 1 * 60 * 60
    // * 1000; 1 min
    long end = System.currentTimeMillis() + 1 * 60 * 60 * 1000;
    BlockView block = new BlockView(getApplicationContext(), "id1", "Blok číslo 1", start, end,
        false, 1);
    blocks.addBlock(block);
  }
}
