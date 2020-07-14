package android.example.myshop.ViewHolder;

import android.example.myshop.Interface.ItemClickListener;
import android.example.myshop.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductName, txtproductDescription,txtproductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.product_image);
        txtproductName = itemView.findViewById(R.id.product_name);
        txtproductDescription = itemView.findViewById(R.id.product_description);
        txtproductPrice = itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
