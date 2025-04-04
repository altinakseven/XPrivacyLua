package eu.faircode.xlua;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Dummy implementation of AdapterApp for compilation purposes
 */
public class AdapterApp extends RecyclerView.Adapter<AdapterApp.ViewHolder> {
    public enum enumShow {
        none, user, icon, all
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Dummy implementation
    }
    
    @Override
    public int getItemCount() {
        return 0;
    }
}
