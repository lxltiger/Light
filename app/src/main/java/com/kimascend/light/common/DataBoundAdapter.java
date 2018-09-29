package com.kimascend.light.common;

import android.annotation.SuppressLint;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class DataBoundAdapter<T,V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>>{

    private List<T> items;
    private int version;


    @SuppressLint("StaticFieldLeak")
    public void replace(List<T> update) {
        if (items == null) {
            if (update == null) {
                return;
            }
            items=update;
            notifyDataSetChanged();
        } else if (update == null) {
            int size=getItemCount();
            notifyItemRangeRemoved(0, size);
            items = null;
        }else{
            version++;
            final List<T> old=items;
            final int flag=version;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                   return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return old.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = old.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundAdapter.this.areItemsTheSame(oldItem,newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = old.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundAdapter.this.areContentsTheSame(oldItem,newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (flag != version) {
                        return;
                    }
                    diffResult.dispatchUpdatesTo(DataBoundAdapter.this);
                    items=update;

                }
            }.execute();
        }
    }

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding= createBinding(parent);
        return new DataBoundViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, items.get(position));
        holder.binding.executePendingBindings();
    }

    protected void add(T item) {
        if (item == null) {
            return;
        }
        if (items == null) {
            items =new ArrayList<>();
        }
        items.add(item);
        notifyItemChanged(getItemCount() - 1);
    }

    protected void set(List<T> update) {
        if (update == null) {
            if (items == null) {
                return;
            }
            int size=items.size();
            notifyItemRangeChanged(0, size);
            items=null;
        }else{
            items=update;
            notifyDataSetChanged();
        }

    }

    protected T get(Predicate<T> predicate) {
        if (items != null) {
            for (T item : items) {
                if (predicate.test(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    protected void clear() {
        if (items != null) {
            items.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return items==null?0:items.size();
    }

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    protected abstract void bind(V binding,T item);

    protected abstract V createBinding(ViewGroup parent);


}
